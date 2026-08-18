package com.eight64zeros.clearstreak.security

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher

class DatabasePassphraseProvider(private val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val isPassphraseInitialized: Boolean
        get() = prefs.contains(KEY_ENCRYPTED_PASSPHRASE) && prefs.contains(KEY_IV)

    fun getStoredIv(): ByteArray? {
        val ivStr = prefs.getString(KEY_IV, null) ?: return null
        return Base64.decode(ivStr, Base64.NO_WRAP)
    }

    /**
     * Called during initial enrollment / setup: generates a 256-bit passphrase,
     * encrypts it using the authenticated Cipher, and stores ciphertext + IV.
     */
    fun initializePassphrase(authenticatedCipher: Cipher): ByteArray {
        val rawPassphrase = ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }
        val encryptedPassphrase = authenticatedCipher.doFinal(rawPassphrase)
        val iv = authenticatedCipher.iv

        prefs.edit()
            .putString(KEY_ENCRYPTED_PASSPHRASE, Base64.encodeToString(encryptedPassphrase, Base64.NO_WRAP))
            .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()

        return rawPassphrase
    }

    /**
     * Decrypts the stored database passphrase using the BiometricPrompt-authenticated Cipher.
     */
    fun unlockPassphrase(authenticatedCipher: Cipher): ByteArray {
        val encryptedStr = prefs.getString(KEY_ENCRYPTED_PASSPHRASE, null)
            ?: throw IllegalStateException("Database passphrase has not been initialized.")
        val encryptedBytes = Base64.decode(encryptedStr, Base64.NO_WRAP)
        return authenticatedCipher.doFinal(encryptedBytes)
    }

    companion object {
        private const val PREFS_NAME = "clearstreak_secure_prefs"
        private const val KEY_ENCRYPTED_PASSPHRASE = "enc_db_pass"
        private const val KEY_IV = "enc_db_iv"
    }
}
