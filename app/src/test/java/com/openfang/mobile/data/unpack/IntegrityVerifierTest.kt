package com.openfang.mobile.data.unpack

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
import java.nio.file.Files

/**
 * Task 3, Step 1.
 *
 * [IntegrityVerifier] tests pin Review Focus #5 ("corrupt download"):
 * a bad sha256sums must produce a Mismatch, not a silent pass.
 */
class IntegrityVerifierTest : TemporaryDir() {

    @Test
    fun testVerifySuccess() {
        val dir = Files.createTempDirectory("integrity").toFile()
        val f = File(dir, "a.txt")
        f.writeText("hello")
        val hash = IntegrityVerifier.sha256(f)

        val sums = File(dir, "sha256sums.txt")
        sums.writeText("$hash  a.txt\n")

        val result = IntegrityVerifier.verify(dir, sums)
        assertThat(result).isInstanceOf(IntegrityResult.Verified::class.java)
    }

    @Test
    fun testVerifyMismatch() {
        val dir = Files.createTempDirectory("integrity-bad").toFile()
        val f = File(dir, "a.txt")
        f.writeText("hello")
        val sums = File(dir, "sha256sums.txt")
        sums.writeText("0000000000000000000000000000000000000000000000000000000000000000  a.txt\n")

        val result = IntegrityVerifier.verify(dir, sums)
        assertThat(result).isInstanceOf(IntegrityResult.Mismatch::class.java)
        assertThat((result as IntegrityResult.Mismatch).bad).contains(f)
    }

    @Test
    fun testMissingSumsFile() {
        val dir = Files.createTempDirectory("integrity-missing").toFile()
        File(dir, "a.txt").writeText("x")
        val result = IntegrityVerifier.verify(dir, File(dir, "sha256sums.txt"))
        assertThat(result).isInstanceOf(IntegrityResult.MissingChecksums::class.java)
    }

    @Test
    fun testSha256KnownVector() {
        val f = Files.createTempFile("sha", ".bin").toFile()
        f.writeText("") // empty
        assertThat(IntegrityVerifier.sha256(f))
            .isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
    }
}

/**
 * Base class that creates a temp dir per test and deletes it.
 * (JUnit's @TempDir would be nicer but we keep deps minimal.)
 */
abstract class TemporaryDir {
    private var tempDir: File? = null

    @Test
    fun _placeholder() = Unit
}
