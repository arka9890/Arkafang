package com.openfang.mobile.data.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A suspendable, resumable HTTP downloader. Implementations MUST support
 * resuming from a partial file (HTTP Range: bytes=<size>-) so that a
 * flaky network doesn't restart a ~150 MB download from 0.
 *
 * @see EnvironmentDownloader for the concrete Ktor-based implementation.
 */
interface Downloader {
    /**
     * Download [url] to [target]. If [target] already exists with size S,
     * the request MUST send `Range: bytes=S-` and append to the file.
     *
     * @param progress  callback with bytes-downloaded-so-far / total
     *                  (total may be unknown → use -1)
     * @return  true on success, false if the mirror failed (caller
     *          should try the next mirror)
     */
    suspend fun download(url: String, target: java.io.File, progress: (Long) -> Unit): Boolean
}
