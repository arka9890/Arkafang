package com.openfang.mobile.data.unpack

import java.io.File
import java.security.MessageDigest

/**
 * Result of an integrity check.
 */
sealed class IntegrityResult {
    data class Verified(val fileCount: Int) : IntegrityResult()
    data class Mismatch(val bad: List<File>) : IntegrityResult()
    data class MissingChecksums(val expected: File) : IntegrityResult()
}

/**
 * Verify files in [dir] against a `sha256sums.txt` (one
 * `<hex>  <filename>` per line, relative to [dir]).
 *
 * The checksums file MUST be downloaded alongside the payload; the
 * [expected] argument is the local path to that file.
 */
object IntegrityVerifier {

    fun verify(dir: File, expected: File): IntegrityResult {
        if (!expected.exists()) return IntegrityResult.MissingChecksums(expected)
        val lines = expected.readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
        if (lines.isEmpty()) return IntegrityResult.MissingChecksums(expected)

        val bad = mutableListOf<File>()
        var checked = 0
        for (line in lines) {
            val parts = line.split(Regex("\\s+"))
            if (parts.size < 2) continue
            val hash = parts[0].lowercase()
            val fname = parts[1]
            val f = File(dir, fname)
            if (!f.exists()) {
                bad += f
                continue
            }
            val actual = sha256(f)
            if (actual != hash) bad += f
            checked++
        }
        return if (bad.isEmpty()) IntegrityResult.Verified(checked)
        else IntegrityResult.Mismatch(bad)
    }

    /**
     * SHA-256 of a file, lowercase hex.
     */
    fun sha256(f: File): String {
        val md = MessageDigest.getInstance("SHA-256")
        f.inputStream().use { ins ->
            val buf = ByteArray(64 * 1024)
            var n = ins.read(buf)
            while (n > 0) {
                md.update(buf, 0, n)
                n = ins.read(buf)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
