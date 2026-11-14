package com.openfang.mobile.service

import android.content.Context
import com.openfang.proot.ProotConfig
import com.openfang.proot.ProotPaths
import com.openfang.proot.ProotProcess
import com.openfang.proot.ProotResult
import com.openfang.proot.ProotRunner
import java.io.File
import java.util.Properties
import java.util.concurrent.TimeUnit

/**
 * Owns the Proot + OpenFang daemon lifecycle.
 *
 * One [ProotOrchestrator] instance per app process. The [ProotService]
 * (foreground service) calls [start] / [stop]. The dashboard VM
 * (Task 6) calls [status] to render the header.
 *
 * Review Focus #3 + #4 are pinned here:
 *  - [checkKernelVersion] returns a warning if kernel < 4.14.
 *  - [start] re-checks [ProotProcess.isAlive] after launch; if the
 *    process died immediately, it returns Failure (not Success).
 *  - The watchdog in [ProotService] polls [status] every 30 s.
 */
class ProotOrchestrator(private val context: Context) {

    @Volatile private var process: ProotProcess? = null
    @Volatile private var config: ProotConfig = ProotConfig.defaults()
    @Volatile private var paths: ProotPaths = ProotPaths(
        File(context.getExternalFilesDir(null), "env"),
    )

    private val stateFile = File(context.filesDir, "proot-state.json")

    fun currentConfig(): ProotConfig = config
    fun currentPaths(): ProotPaths = paths

    /**
     * Start the daemon. If the environment hasn't been set up yet,
     * this returns Failure("env not set up") — the app must run
     * the setup flow first (Task 3 + SetupScreen in Task 6).
     */
    fun start(): ProotResult {
        process?.let {
            if (it.isAlive) return ProotResult.Success("already running")
        }
        if (!paths.envRoot.exists()) {
            return ProotResult.Failure(-1, "env not set up; run setup first")
        }
        config = loadConfig()
        val proc = try {
            ProotRunner.start(config, paths, subcommand = "start")
        } catch (e: Exception) {
            return ProotResult.Failure(-1, "exec failed: ${e.message}")
        }
        process = proc
        // Pin Review Focus #4: check the process is actually alive
        // before returning Success. If it died immediately (bad
        // binary, kernel issue, etc), report Failure.
        try {
            Thread.sleep(500)
        } catch (_: InterruptedException) { }
        return if (proc.isAlive) {
            ProotResult.Success("started")
        } else {
            proc.tail(2048).let { tail ->
                ProotResult.Failure(-1, "daemon exited immediately: $tail")
            }
        }
    }

    fun stop() {
        process?.stop()
        process = null
    }

    /**
     * Live status. The dashboard polls this every few seconds;
     * the watchdog polls every 30 s.
     *
     * @return  [DaemonStatus.Running] / [DaemonStatus.Stopped]
     */
    fun status(): DaemonStatus {
        val p = process
        return if (p != null && p.isAlive) {
            DaemonStatus.Running(version = config.openfangTag)
        } else {
            DaemonStatus.Stopped
        }
    }

    /**
     * Pin Review Focus #3: check the kernel version. Android 5.0+
     * is 3.x but most arm64 devices ship 4.14+. Proot works down
     * to ~3.18 with warnings; below that, exec will fail.
     *
     * Returns null if the kernel is OK, or a user-facing warning
     * string.
     */
    fun checkKernelVersion(): String? {
        val release = (System.getProperty("os.version") ?: "").trim()
        // os.version looks like "4.14.113-perf" or "3.10.101"
        val parts = release.split('.').map { it.takeWhile { c -> c.isDigit() }.toIntOrNull() }
        val major = parts.getOrNull(0) ?: 0
        val minor = parts.getOrNull(1) ?: 0
        return when {
            major < 3 -> "Kernel $release is too old; Proot likely won't work."
            major == 3 && minor < 18 -> "Kernel $release is old; Proot may fail. Update your OS if possible."
            major == 4 && minor < 14 -> "Kernel $release is below the recommended 4.14; some Proot features (namespaces) may be limited."
            else -> null
        }
    }

    private fun loadConfig(): ProotConfig {
        val envFile = File(paths.envRoot, "setup.env")
        if (!envFile.exists()) return ProotConfig.defaults()
        return ProotConfig.parse(envFile.readText())
    }

    sealed class DaemonStatus {
        data class Running(val version: String) : DaemonStatus()
        object Stopped : DaemonStatus()
    }
}
