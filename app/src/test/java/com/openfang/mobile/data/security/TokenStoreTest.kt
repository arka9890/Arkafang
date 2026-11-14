package com.openfang.mobile.data.security

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Task 9: TokenStore test.
 *
 * We can't fully exercise EncryptedSharedPreferences in a JVM unit
 * test (it needs the Android Keystore), so this test pins the
 * *contract*: that the class compiles and the methods have the
 * expected shape. The actual encryption at rest is verified by the
 * E2E test in Task 9 Step 4 (manual device check).
 */
class TokenStoreTest {

    @Test
    fun testTokenStoreClassExists() {
        // Compile-time check: the class is present and the methods
        // have the right signatures.
        val methods = TokenStore::class.java.declaredMethods.map { it.name }
        assertThat(methods).containsAllOf("set", "get", "clear")
    }
}
