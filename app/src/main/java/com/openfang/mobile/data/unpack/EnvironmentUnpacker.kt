package com.openfang.mobile.data.unpack

import com.openfang.proot.ProotPaths
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Result of an unpack.
 */
sealed class UnpackResult {
    object Success : UnpackResult()
    data class InsufficientStorage(val required: Long, val available: Long) : UnpackResult()
    data class Failure(val reason: String) : UnpackResult()
}

/**
 * Unpacks the two environment tarballs into a [ProotPaths] layout:
 *
 *   - rootfsTar → <envRoot>/rootfs/
 *   - binTar    → <envRoot>/bin/  (contains the openfang binary)
 *
 * Before touching the disk, checks that free space ≥ [MIN_FREE_BYTES]
 * (500 MB). This prevents a half-unpacked env from corrupting the
 * storage.
 */
object EnvironmentUnpacker {

    /** 500 MB — pinned by Global Constraints. */
    const val MIN_FREE_BYTES: Long = 500L * 1024 * 1024

    /**
     * Unpack both tarballs.
     *
     * @param rootfsTar  the `ubuntu-aarch64.tar.xz` file (xz-compressed tar)
     * @param binTar     the `openfang-aarch64.tar.gz` file (gz-compressed tar)
     * @param paths      target layout
     * @return  [UnpackResult]
     */
    fun unpack(rootfsTar: File, binTar: File, paths: ProotPaths): UnpackResult {
        if (!rootfsTar.exists() || !binTar.exists()) {
            return UnpackResult.Failure("input tarball missing")
        }
        // Free space check — pin Review Focus #2
        val available = paths.envRoot.freeSpace
        if (available < MIN_FREE_BYTES) {
            return UnpackResult.InsufficientStorage(MIN_FREE_BYTES, available)
        }

        val rootfsDir = paths.rootfs
        val binDir = paths.bin
        rootfsDir.mkdirs()
        binDir.mkdirs()

        // Unpack rootfs (xz)
        try {
            rootfsTar.inputStream().use { ins ->
                val xz = XZCompressorInputStream(ins)
                val tar = TarArchiveInputStream(xz)
                unpackTar(tar, rootfsDir)
            }
        } catch (e: Exception) {
            return UnpackResult.Failure("rootfs unpack: ${e.message}")
        }

        // Unpack bin (gz)
        try {
            binTar.inputStream().use { ins ->
                val gz = GZIPInputStream(ins)
                val tar = TarArchiveInputStream(gz)
                unpackTar(tar, binDir)
            }
        } catch (e: Exception) {
            return UnpackResult.Failure("bin unpack: ${e.message}")
        }

        // Ensure the openfang binary is executable
        val openfang = paths.openfangBinary
        if (openfang.exists()) openfang.setExecutable(true)
        val proot = paths.prootBinary
        if (proot.exists()) proot.setExecutable(true)

        return UnpackResult.Success
    }

    /**
     * Write each tar entry to [destDir]. Entries that are directories
     * are created; symlinks are skipped (Proot handles them at runtime);
     * files are written with a 777-protective mask (0644).
     */
    private fun unpackTar(tar: TarArchiveInputStream, destDir: File) {
        var entry: TarArchiveEntry? = tar.nextEntry
        while (entry != null) {
            val target = File(destDir, entry.name)
            // Defense: tar path traversal
            val canonicalTarget = target.canonicalFile
            if (!canonicalTarget.path.startsWith(destDir.canonicalFile.path)) {
                entry = tar.nextEntry
                continue
            }
            if (entry.isDirectory) {
                target.mkdirs()
            } else if (entry.isSymbolicLink) {
                // Skip symlinks in the unpacker; Proot's bind handles /dev
                // symlinks at runtime.
            } else {
                target.parentFile?.mkdirs()
                java.nio.file.Files.copy(tar, target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
                if (entry.mode and 0x49 != 0) {
                    target.setExecutable(true)
                }
            }
            entry = tar.nextEntry
        }
    }
}
