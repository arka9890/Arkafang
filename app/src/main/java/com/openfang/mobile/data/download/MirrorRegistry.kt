package com.openfang.mobile.data.download

import java.io.File

/**
 * A concrete mirror registry, built from `res/raw/mirrors.json`.
 *
 * Mirrors are tried in the order given. If [downloadFile] is called for
 * a file that doesn't exist on mirror #1, the caller should retry on
 * mirror #2, etc.
 */
class MirrorRegistry(
    val mirrors: List<Mirror>,
) {

    companion object {
        /**
         * Default mirrors, matching `mirrors.json` shipped in the APK.
         * The "user" mirror is read from [userMirrorOverride] at runtime.
         */
        fun defaults(userMirrorOverride: String? = null): MirrorRegistry =
            MirrorRegistry(
                listOf(
                    Mirror("official", "https://openfang.sh/env/"),
                    Mirror(
                        "github-releases",
                        "https://github.com/arka9890/openfang-android/releases/download/v0.6.9/",
                    ),
                    Mirror("user", userMirrorOverride.orEmpty()),
                ).filter { it.base.isNotEmpty() },
            )

        /**
         * The four environment files, in dependency order (rootfs first).
         */
        val ENVIRONMENT_FILES = listOf(
            "ubuntu-aarch64.tar.xz",
            "openfang-aarch64.tar.gz",
            "sha256sums.txt",
            "setup.env",
        )
    }

    /**
     * Download [name] from the first mirror that succeeds. Returns the
     * local file path on success.
     *
     * @param progress  per-file progress callback (bytes-so-far)
     * @param cancel   a check called every download tick; if it returns
     *                 true, the download is aborted (no exception).
     */
    suspend fun downloadFile(
        name: String,
        target: File,
        progress: (Long) -> Unit,
        cancel: () -> Boolean = { false },
    ): DownloadResult {
        val failures = mutableListOf<MirrorFailure>()
        for (mirror in mirrors) {
            if (cancel()) break
            val url = mirror.base.trimEnd('/') + "/" + name
            val ok = try {
                downloader.download(url, target, progress)
            } catch (e: Exception) {
                false
            }
            if (ok) {
                return DownloadResult.Success(target)
            }
            val partial = if (target.exists()) target.length() else 0
            failures += MirrorFailure(mirror.name, "download failed ($url)", partial)
            // Don't delete a partial file; the next mirror will resume
            // from it (HTTP Range). If the next mirror 404s, we move on
            // to the next.
        }
        return DownloadResult.Failure(failures)
    }

    /**
     * The [Downloader] used. Injectable for unit testing.
     */
    var downloader: Downloader = EnvironmentDownloader()
}
