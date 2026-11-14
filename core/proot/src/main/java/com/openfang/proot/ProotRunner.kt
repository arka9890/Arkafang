package com.openfang.proot

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Result of a Proot exec. Either [Success] (exit 0) or [Failure] (non-zero
 * exit + stderr snippet).
 */
sealed class ProotResult {
    data class Success(val stdout: String) : ProotResult()
    data class Failure(val code: Int, val message: String) : ProotResult()
}

/**
 * A handle to a running Proot process.
 *
 * @property pid    process id (best-effort; -1 if unknown)
 * @property stop() gracefully stop (SIGTERM, then SIGKILL after 5s)
 */
class ProotProcess internal constructor(
    private val process: Process,
    val pid: Int,
    private val stopCalled: () -> Unit = {}
) {
    val isAlive: Boolean get() = process.isAlive

    fun stop() {
        stopCalled()
        process.destroy()
        val done = process.waitFor(5, TimeUnit.SECONDS)
        if (!done) process.destroyForcibly()
    }

    /** Tail of the in-memory ring buffer of stdout/stderr. */
    fun tail(maxBytes: Int = 4096): String {
        return LogRingBuffer.tail(pid, maxBytes)
    }
}

/**
 * Minimal in-memory ring buffer keyed by PID. Each Proot process's stdout
 * and stderr are appended here; the most recent [cap] bytes are kept.
 * The app layer reads this to populate the Logs screen.
 *
 * The buffer also persists to `logs/openfang.log` on a best-effort basis
 * (file I/O failures are swallowed — this is a log, not a critical path).
 */
object LogRingBuffer {
    private val buffer = HashMap<Int, ByteArray>()
    private const val CAP = 1024 * 1024 // 1 MB

    @Volatile
    var persistTo: File? = null

    fun append(pid: Int, bytes: ByteArray) {
        val existing = synchronized(this) { buffer[pid] } ?: ByteArray(0)
        val merged = if (existing.size + bytes.size <= CAP) {
            existing + bytes
        } else {
            bytes.copyOfRange((bytes.size - CAP).coerceAtLeast(0), bytes.size)
        }
        synchronized(this) { buffer[pid] = merged }
        persistTo?.let { f ->
            try {
                if (!f.parentFile.exists()) f.parentFile.mkdirs()
                f.appendBytes(bytes)
            } catch (_: Exception) { /* swallow */ }
        }
    }

    fun tail(pid: Int, maxBytes: Int = 4096): String {
        val bytes = synchronized(this) { buffer[pid] } ?: ByteArray(0)
        val start = (bytes.size - maxBytes).coerceAtLeast(0)
        return String(bytes, start, bytes.size - start)
    }
}

/**
 * Builds and execs the Proot command.
 *
 * The [buildCommand] function is pure (no process side effects) so it's
 * unit-testable. [start] actually launches the process.
 *
 * Subcommands:
 * - "start"     → `openfang start --port <port> --no-browser`
 * - "version"   → `openfang --version` (smoke test)
 * - "stop"      → `openfang stop` (not used; we kill the process instead)
 */
object ProotRunner {

    /**
     * Build the full command-line for a Proot exec.
     *
     * @param subcommand  what to run inside the rootfs ("start", "version", …)
     */
    fun buildCommand(
        config: ProotConfig,
        paths: ProotPaths,
        subcommand: String = "start",
    ): List<String> {
        val cmd = mutableListOf(
            paths.prootBinary.absolutePath,
            "-r", paths.rootfs.absolutePath,
        )
        for (bind in config.bindPaths) {
            cmd += "-b"
            cmd += bind
        }
        cmd += "-w"
        cmd += "/home/openfang"
        cmd += "-0"  // make pid 0 be the init (so proot can spawn daemons)

        cmd += paths.openfangBinary.absolutePath
        when (subcommand) {
            "version" -> cmd += "--version"
            "start" -> {
                cmd += "start"
                cmd += "--port"
                cmd += config.listenPort.toString()
                cmd += "--no-browser"
            }
            else -> cmd += subcommand
        }
        return cmd
    }

    /**
     * Parse a process's exit code + stdout into a [ProotResult].
     */
    fun parseProotResult(stdout: String, exitCode: Int): ProotResult {
        return if (exitCode == 0) {
            ProotResult.Success(stdout)
        } else {
            ProotResult.Failure(exitCode, stdout.trim())
        }
    }

    /**
     * Launch Proot + OpenFang in a sub-process. Returns a [ProotProcess]
     * handle. Caller is responsible for calling [ProotProcess.stop].
     *
     * This function does NOT block — it returns immediately with the
     * process handle. Output is streamed into [LogRingBuffer].
     */
    fun start(config: ProotConfig, paths: ProotPaths, subcommand: String = "start"): ProotProcess {
        val cmd = buildCommand(config, paths, subcommand)
        val pb = ProcessBuilder(cmd).redirectErrorStream(true)
        pb.environment()["PATH"] = "/usr/bin:/bin"
        paths.logs.mkdirs()
        LogRingBuffer.persistTo = paths.openfangLog

        val process = pb.start()
        val pid = -1  // Android ProcessBuilder doesn't expose pid easily
        // Drain stdout in a background thread so the buffer fills up.
        val t = Thread({
            try {
                process.inputStream.use { ins ->
                    val buf = ByteArray(8192)
                    while (true) {
                        val n = ins.read(buf)
                        if (n < 0) break
                        LogRingBuffer.append(pid, buf.copyOf(n))
                    }
                }
            } catch (_: Exception) { }
        }, "proot-stdout")
        t.isDaemon = true
        t.start()

        return ProotProcess(process, pid)
    }
}
