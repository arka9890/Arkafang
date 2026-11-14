package com.openfang.mobile.data.download

/**
 * A mirror registry entry. Order in [MirrorRegistry.mirrors] defines
 * the try-order: official → GitHub Releases → user.
 *
 * @property name  human-readable identifier
 * @property base  base URL (trailing slash required)
 *
 * The actual files to download are listed separately in
 * `res/raw/mirrors.json`.
 */
data class Mirror(val name: String, val base: String)
