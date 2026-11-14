package com.openfang.proot

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProotConfigTest {

    @Test
    fun testDefaults() {
        val c = ProotConfig.defaults()
        assertThat(c.openfangTag).isEqualTo("v0.6.9")
        assertThat(c.listenPort).isEqualTo(4200)
        assertThat(c.bindPaths).containsAtLeast("/dev", "/proc", "/sys")
    }

    @Test
    fun testParseMinimal() {
        val c = ProotConfig.parse("openfangTag=v0.7.0\nlistenPort=5000\n")
        assertThat(c.openfangTag).isEqualTo("v0.7.0")
        assertThat(c.listenPort).isEqualTo(5000)
        // bindPaths not set, so default
        assertThat(c.bindPaths).contains("/dev")
    }

    @Test
    fun testParseWithCommentsAndBlank() {
        val c = ProotConfig.parse(
            "# comment\n" +
            "  \n" +
            "openfangTag=v0.6.9\n" +
            "listenPort=4200\n"
        )
        assertThat(c.openfangTag).isEqualTo("v0.6.9")
        assertThat(c.listenPort).isEqualTo(4200)
    }

    @Test
    fun testParseInvalidPortKeepsDefault() {
        val c = ProotConfig.parse("listenPort=notanumber\n")
        assertThat(c.listenPort).isEqualTo(4200)
    }

    @Test
    fun testParseBindPaths() {
        val c = ProotConfig.parse("bindPaths=/dev,/proc,/tmp\n")
        assertThat(c.bindPaths).isEqualTo(listOf("/dev", "/proc", "/tmp"))
    }
}
