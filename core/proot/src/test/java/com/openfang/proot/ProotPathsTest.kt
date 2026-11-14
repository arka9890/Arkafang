package com.openfang.proot

import java.io.File
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Task 2, Step 1.
 *
 * Tests [ProotPaths] derivation from an env root dir. The layout is
 * pinned by the spec §6.4:
 *
 *     <envRoot>/
 *       ├── bin/                # proot + openfang binaries
 *       ├── rootfs/             # unpacked Ubuntu rootfs
 *       ├── home/openfang/      # daemon's home
 *       ├── logs/               # openfang.log + proot.log
 *       └── state/              # openfang's SQLite + config
 */
class ProotPathsTest {

    @Test
    fun testDerivedPathsFromEnvRoot() {
        val root = File("/data/data/com.openfang.mobile/files/env")
        val p = ProotPaths(root)

        assertThat(p.bin).isEqualTo(File(root, "bin"))
        assertThat(p.rootfs).isEqualTo(File(root, "rootfs"))
        assertThat(p.home).isEqualTo(File(root, "home/openfang"))
        assertThat(p.logs).isEqualTo(File(root, "logs"))
        assertThat(p.stateDir).isEqualTo(File(root, "state"))
    }

    @Test
    fun testProotBinaryPath() {
        val root = File("/sdcard/openfang/env")
        val p = ProotPaths(root)
        assertThat(p.prootBinary).isEqualTo(File(root, "bin/proot"))
        assertThat(p.openfangBinary).isEqualTo(File(root, "bin/openfang"))
    }
}
