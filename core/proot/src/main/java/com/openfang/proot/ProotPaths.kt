package com.openfang.proot

import java.io.File

/**
 * All file-system paths for a Proot environment, derived from a single
 * root directory. The layout is fixed (see [ProotPathsTest] and spec §6.4):
 *
 *     <envRoot>/
 *       ├── bin/proot           # the proot binary (cross-compiled in CI)
 *       ├── bin/openfang       # the OpenFang binary (downloaded at setup)
 *       ├── rootfs/            # unpacked Ubuntu rootfs
 *       ├── home/openfang/     # daemon home
 *       ├── logs/              # openfang.log, proot.log
 *       └── state/             # openfang's SQLite DB + config
 *
 * All paths are [File] references; they are NOT created at construction.
 * The [EnvironmentUnpacker] (Task 3) is responsible for creating them.
 */
data class ProotPaths(val envRoot: File) {

    val bin: File get() = File(envRoot, "bin")
    val prootBinary: File get() = File(bin, "proot")
    val openfangBinary: File get() = File(bin, "openfang")

    val rootfs: File get() = File(envRoot, "rootfs")

    val home: File get() = File(File(envRoot, "home"), "openfang")

    val logs: File get() = File(envRoot, "logs")
    val openfangLog: File get() = File(logs, "openfang.log")
    val prootLog: File get() = File(logs, "proot.log")

    val stateDir: File get() = File(envRoot, "state")
}
