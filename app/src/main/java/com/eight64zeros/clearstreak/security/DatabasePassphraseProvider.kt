package com.eight64zeros.clearstreak.security

import android.content.Context
import android.util.Base64
import java.security.SecureRandom

/**
 * Generates and stores the SQLCipher database passphrase, wrapped by the auth-bound Keystore
 * master key. Callers must have just passed device authentication (biometric or credential)
 * before calling [initializePassphrase] / [unlockPassphrase] — the key is only usable inside
 * that short window.
 */
class DatabasePassphraseProvider(private val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val isPassphraseInitialized: Boolean
        get() = prefs.contains(KEY_ENCRYPTED_PASSPHRASE) && prefs.contains(KEY_IV)

    /** First run: generate a 256-bit passphrase, wrap it with the master key, store ciphertext + IV. */
    fun initializePassphrase(): ByteArray {
        val rawPassphrase = ByteArray(32).apply { SecureRandom().nextBytes(this) }
        val (ciphertext, iv) = KeyStoreManager.encrypt(rawPassphrase)
        prefs.edit()
            .putString(KEY_ENCRYPTED_PASSPHRASE, Base64.encodeToString(ciphertext, Base64.NO_WRAP))
            .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()
        return rawPassphrase
    }

    /** Decrypt the stored database passphrase (call within the auth window). */
    fun unlockPassphrase(): ByteArray {
        val encryptedStr = prefs.getString(KEY_ENCRYPTED_PASSPHRASE, null)
            ?: throw IllegalStateException("Database passphrase has not been initialized.")
        val ivStr = prefs.getString(KEY_IV, null)
            ?: throw IllegalStateException("Passphrase IV missing.")
        return KeyStoreManager.decrypt(
            Base64.decode(encryptedStr, Base64.NO_WRAP),
            Base64.decode(ivStr, Base64.NO_WRAP)
        )
    }

    /** Clear the stored (now-unopenable) passphrase, e.g. after key invalidation. */
    fun reset() {
        prefs.edit().remove(KEY_ENCRYPTED_PASSPHRASE).remove(KEY_IV).apply()
    }

    companion object {
        private const val PREFS_NAME = "clearstreak_secure_prefs"
        private const val KEY_ENCRYPTED_PASSPHRASE = "enc_db_pass"
        private const val KEY_IV = "enc_db_iv"
    }
}
