package com.openfang.mobile.data.download

/**
 * Result of a single mirror download attempt.
 */
sealed class DownloadResult {
    /** Successfully downloaded (with resume, if any). [targetFile] holds the full file. */
    data class Success(val targetFile: java.io.File) : DownloadResult()
    /**
     * All mirrors exhausted. [attempts] records each mirror's failure
     * (partial download or HTTP error) so the user can see what happened.
     */
    data class Failure(val attempts: List<MirrorFailure>) : DownloadResult()
}

data class MirrorFailure(
    val mirror: String,
    val message: String,
    val bytesDownloaded: Long,
)
