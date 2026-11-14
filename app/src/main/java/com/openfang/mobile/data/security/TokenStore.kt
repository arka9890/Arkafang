package com.openfang.mobile.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Stores the OpenFang API token in [EncryptedSharedPreferences].
 *
 * The token is used to authenticate against the OpenFang daemon
 * (see [com.openfang.api.OpenFangApiClient.tokenProvider]).
 *
 * At-rest protection:
 *  - Backed by Android Keystore (MasterKey AES-256)
 *  - The SharedPreferences file is encrypted; on-device read without
 *    the key returns garbage
 *
 * The token is NOT logged, NOT in plain prefs, NOT in a file.
 */
class TokenStore(context: Context) {

    private val prefs: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "openfang_token",
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    fun set(token: String) {
        prefs.edit().putString("api_token", token).apply()
    }

    fun get(): String? = prefs.getString("api_token", null)

    fun clear() {
        prefs.edit().remove("api_token").apply()
    }
}
