package com.openfang.mobile.data

import kotlinx.serialization.Serializable

/**
 * Persisted state of the one-time setup flow.
 *
 * [complete]    true once download+unpack+integrity+smoke-test all pass.
 * [envVersion]  the OpenFang tag (e.g. "v0.6.9") the env was set up for.
 *               On app launch, if this differs from the latest mirror's
 *               tag, the update flow (spec §6.9) triggers a re-download.
 * [checksumOk]  true if the last integrity check passed.
 */
@Serializable
data class SetupState(
    val complete: Boolean = false,
    val envVersion: String = "v0.6.9",
    val checksumOk: Boolean = false,
)

/**
 * In-memory + SharedPreferences-backed SetupState holder.
 *
 * SharedPreferences is sufficient here (small, non-sensitive). The
 * API token (which IS sensitive) goes to [com.openfang.mobile.data.security.TokenStore]
 * in Task 9.
 */
class SetupStateStore(androidContext: android.content.Context) {

    private val prefs = androidContext.getSharedPreferences("setup_state", android.content.Context.MODE_PRIVATE)

    fun load(): SetupState {
        return SetupState(
            complete = prefs.getBoolean("complete", false),
            envVersion = prefs.getString("envVersion", "v0.6.9") ?: "v0.6.9",
            checksumOk = prefs.getBoolean("checksumOk", false),
        )
    }

    fun save(state: SetupState) {
        prefs.edit()
            .putBoolean("complete", state.complete)
            .putString("envVersion", state.envVersion)
            .putBoolean("checksumOk", state.checksumOk)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
