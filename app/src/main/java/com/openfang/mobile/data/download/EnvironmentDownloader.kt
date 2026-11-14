package com.openfang.mobile.data.download

import java.io.File
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * HttpURLConnection-based [Downloader] with resume support.
 *
 * Sends `Range: bytes=<existing>-` if the target file already has bytes
 * — so a flaky network resumes from the last byte instead of restarting.
 * 404/410 → false (caller tries the next mirror).
 *
 * Progress callback is invoked once with the final byte count.
 */
class EnvironmentDownloader : Downloader {

    override suspend fun download(url: String, target: File, progress: (Long) -> Unit): Boolean {
        return withContext(Dispatchers.Default) {
            target.parentFile?.mkdirs()
            val existing = if (target.exists()) target.length() else 0L

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            if (existing > 0) {
                connection.setRequestProperty("Range", "bytes=$existing-")
            }

            connection.connect()
            val status = connection.responseCode
            if (status == 404 || status == 410) {
                connection.disconnect()
                return@withContext false
            }

            var downloaded = existing
            val raf = RandomAccessFile(target, "rw")
            raf.seek(existing)
            connection.inputStream.use { ins ->
                val buf = ByteArray(8192)
                while (true) {
                    val n = ins.read(buf)
                    if (n < 0) break
                    raf.write(buf, 0, n)
                    downloaded += n
                    progress(downloaded)
                }
            }
            raf.close()
            connection.disconnect()
            return@withContext true
        }
    }
}
