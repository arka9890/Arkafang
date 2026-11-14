package com.openfang.proot

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Task 2, Step 3.
 *
 * Tests [ProotRunner.buildCommand] (a pure function — testable without
 * actually exec'ing Proot) and [ProotRunner.parseProotResult].
 */
class ProotRunnerTest {

    @Test
    fun testBuildCommandStarts() {
        val cfg = ProotConfig.defaults()
        val paths = com.openfang.proot.ProotPaths(java.io.File("/tmp/env"))
        val cmd = ProotRunner.buildCommand(cfg, paths)

        // First arg is the proot binary
        assertThat(cmd.first()).isEqualTo("/tmp/env/bin/proot")

        // Contains -r <rootfs>
        val rIdx = cmd.indexOf("-r")
        assertThat(rIdx).isGreaterThan(0)
        assertThat(cmd[rIdx + 1]).isEqualTo("/tmp/env/rootfs")

        // Binds /dev, /proc, /sys
        assertThat(cmd).containsAllOf("-b", "/dev")
        assertThat(cmd).containsAllOf("-b", "/proc")
        assertThat(cmd).containsAllOf("-b", "/sys")

        // Changes to openfang home
        assertThat(cmd).containsAllOf("-w", "/home/openfang")

        // The openfang start command
        assertThat(cmd).contains("start")
        assertThat(cmd).contains("--port")
        assertThat(cmd).contains("4200")
        assertThat(cmd).contains("--no-browser")
    }

    @Test
    fun testBuildCommandCustomPort() {
        val cfg = ProotConfig(openfangTag = "v0.6.9", listenPort = 9999)
        val paths = ProotPaths(java.io.File("/tmp/env"))
        val cmd = ProotRunner.buildCommand(cfg, paths)
        assertThat(cmd).contains("9999")
    }

    @Test
    fun testParseProotResultSuccess() {
        val result = ProotRunner.parseProotResult("openfang 0.6.9", 0)
        assertThat(result is ProotResult.Success).isTrue()
    }

    @Test
    fun testParseProotResultFailure() {
        val result = ProotRunner.parseProotResult("permission denied", 13)
        assertThat(result is ProotResult.Failure).isTrue()
        assertThat((result as ProotResult.Failure).code).isEqualTo(13)
        assertThat(result.message).contains("permission")
    }

    @Test
    fun testProotVersionSubcommand() {
        val cfg = ProotConfig.defaults()
        val paths = ProotPaths(java.io.File("/tmp/env"))
        val cmd = ProotRunner.buildCommand(cfg, paths, subcommand = "version")
        assertThat(cmd).contains("--version")
        // Not "start"
        assertThat(cmd).doesNotContain("start")
    }
}
