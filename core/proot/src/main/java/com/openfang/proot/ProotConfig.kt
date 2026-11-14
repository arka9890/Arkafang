package com.openfang.proot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for the Proot + OpenFang environment. Parsed from the
 * `setup.env` file shipped in the GitHub Release (Task 3).
 *
 * @property openfangTag  git tag the OpenFang binary was built from
 *                        (e.g. "v0.6.9"). Drives which mirror assets are
 *                        expected to exist.
 * @property listenPort   port OpenFang daemon listens on. Must be reachable
 *                        from the Android host as 127.0.0.1:port (Proot
 *                        forwards via its own netns).
 * @property bindPaths    additional host paths to bind into the rootfs
 *                        (e.g. /dev, /proc, /sys). Defaults to the
 *                        standard Proot bind set.
 */
@Serializable
data class ProotConfig(
    @SerialName("openfangTag") val openfangTag: String = "v0.6.9",
    @SerialName("listenPort") val listenPort: Int = 4200,
    @SerialName("bindPaths") val bindPaths: List<String> =
        listOf("/dev", "/proc", "/sys", "/sys/."),
) {
    companion object {
        /**
         * Default config used when no `setup.env` is present.
         */
        fun defaults() = ProotConfig()

        /**
         * Parse a `setup.env`-style key=value file. Unknown keys are ignored.
         *
         * Format:
         *   openfangTag=v0.6.9
         *   listenPort=4200
         *   # bindPaths are comma-separated in one line
         *   bindPaths=/dev,/proc,/sys,/sys/.
         */
        fun parse(content: String): ProotConfig {
            var tag = "v0.6.9"
            var port = 4200
            var bindPaths = listOf("/dev", "/proc", "/sys", "/sys/.")
            content.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { line ->
                    val eq = line.indexOf('=')
                    if (eq < 0) return@forEach
                    val key = line.substring(0, eq).trim()
                    val value = line.substring(eq + 1).trim()
                    when (key) {
                        "openfangTag" -> tag = value
                        "listenPort" -> value.toIntOrNull()?.let { port = it }
                        "bindPaths" -> bindPaths = value.split(',').map { it.trim() }
                    }
                }
            return ProotConfig(openfangTag = tag, listenPort = port, bindPaths = bindPaths)
        }
    }
}
