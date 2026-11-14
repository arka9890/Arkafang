package com.openfang.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfang.mobile.data.SetupState
import com.openfang.mobile.data.SetupStateStore
import com.openfang.mobile.data.download.DownloadResult
import com.openfang.mobile.data.download.MirrorRegistry
import com.openfang.mobile.data.unpack.EnvironmentUnpacker
import com.openfang.mobile.data.unpack.IntegrityResult
import com.openfang.mobile.data.unpack.IntegrityVerifier
import com.openfang.mobile.data.unpack.UnpackResult
import com.openfang.proot.ProotConfig
import com.openfang.proot.ProotPaths
import com.openfang.proot.ProotRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for the one-time setup screen (download → unpack → verify →
 * smoke test). Drives the [SetupState] flow in the UI.
 */
class SetupVm(
    private val setupStore: SetupStateStore,
    private val orchestratorScope: CoroutineScope,
    private val envRoot: () -> File,
    private val mirrorRegistry: () -> MirrorRegistry,
) : ViewModel() {

    sealed interface Phase {
        object Idle : Phase
        data class Downloading(val bytes: Long, val fileName: String) : Phase
        object Unpacking : Phase
        object Verifying : Phase
        object Done : Phase
        data class Failed(val reason: String) : Phase
    }

    private val _phase = MutableStateFlow<Phase>(Phase.Idle)
    val phase: StateFlow<Phase> = _phase.asStateFlow()

    /**
     * Kick off the setup flow. Idempotent: if [Phase.Done] or
     * [Phase.Failed], just reset to Idle and run again.
     */
    fun runSetup() {
        orchestratorScope.launch {
            val env = envRoot()
            val tmpDir = File(env, "download")
            tmpDir.mkdirs()

            val rootfsFile = File(tmpDir, "ubuntu-aarch64.tar.xz")
            val binFile = File(tmpDir, "openfang-aarch64.tar.gz")
            val sumsFile = File(tmpDir, "sha256sums.txt")
            val setupEnvFile = File(tmpDir, "setup.env")

            val reg = mirrorRegistry()

            // 1. Download rootfs
            _phase.update { Phase.Downloading(0, "ubuntu-aarch64.tar.xz") }
            val dl = reg.downloadFile(
                name = "ubuntu-aarch64.tar.xz",
                target = rootfsFile,
                progress = { bytes ->
                    _phase.update { Phase.Downloading(bytes, "ubuntu-aarch64.tar.xz") }
                },
            )
            if (dl is DownloadResult.Failure) {
                _phase.update { Phase.Failed("download failed: ${dl.attempts.joinToString(" | ") { it.message }}") }
                return@launch
            }

            // 2. Download remaining files
            val files = listOf(
                "openfang-aarch64.tar.gz" to binFile,
                "sha256sums.txt" to sumsFile,
                "setup.env" to setupEnvFile,
            )
            for ((name, target) in files) {
                _phase.update { Phase.Downloading(0, name) }
                val r = reg.downloadFile(
                    name = name,
                    target = target,
                    progress = { bytes ->
                        _phase.update { Phase.Downloading(bytes, name) }
                    },
                )
                if (r is DownloadResult.Failure) {
                    _phase.update { Phase.Failed("download failed ($name): ${r.attempts.joinToString(" | ") { it.message }}") }
                    return@launch
                }
            }

            // 3. Unpack
            _phase.update { Phase.Unpacking }
            val paths = ProotPaths(env)
            val unpack = EnvironmentUnpacker.unpack(rootfsFile, binFile, paths)
            if (unpack is UnpackResult.InsufficientStorage) {
                _phase.update { Phase.Failed("need ${unpack.required / 1024 / 1024} MB free, have ${unpack.available / 1024 / 1024} MB") }
                return@launch
            }
            if (unpack is UnpackResult.Failure) {
                _phase.update { Phase.Failed("unpack: ${unpack.reason}") }
                return@launch
            }

            // 4. Verify checksums
            _phase.update { Phase.Verifying }
            val integ = IntegrityVerifier.verify(tmpDir, sumsFile)
            when (integ) {
                is IntegrityResult.MissingChecksums -> {
                    _phase.update { Phase.Failed("sha256sums.txt missing") }
                    return@launch
                }
                is IntegrityResult.Mismatch -> {
                    _phase.update { Phase.Failed("checksum mismatch: ${integ.bad.joinToString { it.name }}") }
                    return@launch
                }
                else -> { /* verified */ }
            }

            // 5. Smoke test: proot --version inside the rootfs
            val config = ProotConfig.parse(setupEnvFile.readText())
            val proc = ProotRunner.start(config, paths, subcommand = "version")
            delay(1000)
            proc.stop()
            val tail = proc.tail(512)
            if (!tail.contains("proot") && !tail.contains("version")) {
                _phase.update { Phase.Failed("smoke test failed: $tail") }
                return@launch
            }

            // Persist setup state
            setupStore.save(SetupState(complete = true, envVersion = config.openfangTag, checksumOk = true))
            _phase.update { Phase.Done }
        }
    }

    /** Reset for a retry. */
    fun retry() {
        _phase.update { Phase.Idle }
    }

    override fun onCleared() {
        super.onCleared()
        // orchestratorScope is owned by the app; we don't cancel it here
    }
}
