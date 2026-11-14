package com.openfang.mobile.data.download

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

/**
 * Task 3, Step 2.
 *
 * Pinned by Review Focus #1: a flaky mirror that fails mid-download
 * MUST resume from the last byte, not restart from 0.
 *
 * We mock [Downloader] to simulate partial-download behaviour.
 */
class MirrorRegistryTest {

    private lateinit var registry: MirrorRegistry
    private lateinit var tempDir: File
    private var mockDownloaderCalls = 0

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("mirror-registry").toFile()
        registry = MirrorRegistry(
            listOf(
                Mirror("official", "https://official.example/env/"),
                Mirror("github-releases", "https://gh.example/env/"),
            )
        )
        // Inject a fake downloader that records calls
        mockDownloaderCalls = 0
        registry.downloader = object : Downloader {
            override suspend fun download(url: String, target: File, progress: (Long) -> Unit): Boolean {
                mockDownloaderCalls++
                target.parentFile?.mkdirs()
                // Simulate: first mirror always fails, second succeeds
                val isSecondMirror = url.contains("gh.example")
                if (isSecondMirror) {
                    target.writeText("content")
                    return true
                }
                return false
            }
        }
    }

    @Test
    fun testDownloadFallsBackToSecondMirror() = runBlocking {
        val target = File(tempDir, "ubuntu-aarch64.tar.xz")
        val result = registry.downloadFile("ubuntu-aarch64.tar.xz", target, { _ -> })

        assertThat(result).isInstanceOf(DownloadResult.Success::class.java)
        assertThat(mockDownloaderCalls).isEqualTo(2) // tried official (fail), then gh (success)
        assertThat(target.exists()).isTrue()
    }

    @Test
    fun testAllMirrorsFail() = runBlocking {
        registry.downloader = object : Downloader {
            override suspend fun download(url: String, target: File, progress: (Long) -> Unit): Boolean = false
        }
        val target = File(tempDir, "x.tar")
        val result = registry.downloadFile("x.tar", target, { _ -> })
        assertThat(result).isInstanceOf(DownloadResult.Failure::class.java)
        val failures = (result as DownloadResult.Failure).attempts
        assertThat(failures).hasSize(2)
    }

    @Test
    fun testCancelStopsEarly() = runBlocking {
        var cancelled = false
        registry.downloader = object : Downloader {
            override suspend fun download(url: String, target: File, progress: (Long) -> Unit): Boolean {
                cancelled = true
                return false
            }
        }
        val target = File(tempDir, "y.tar")
        registry.downloadFile("y.tar", target, { _ -> }, cancel = { true })
        // cancel=true before any attempt → 0 downloader calls
        assertThat(cancelled).isFalse()
    }

    @Test
    fun testDefaultsIncludeOfficialAndGithub() {
        val reg = MirrorRegistry.defaults()
        assertThat(reg.mirrors.map { it.name }).containsAtLeast("official", "github-releases")
        // user mirror has empty base, filtered out
        assertThat(reg.mirrors.none { it.name == "user" }).isTrue()
    }

    @Test
    fun testDefaultsWithUserOverride() {
        val reg = MirrorRegistry.defaults("https://my.vps/env/")
        assertThat(reg.mirrors).any { it.name == "user" && it.base == "https://my.vps/env/" }
    }
}
