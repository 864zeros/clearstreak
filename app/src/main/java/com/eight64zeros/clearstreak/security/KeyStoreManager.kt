package com.eight64zeros.clearstreak.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.StrongBoxUnavailableException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Hardware-bound AES-256 master key in Android Keystore (StrongBox with TEE fallback).
 *
 * The key requires user authentication and accepts **either a strong biometric OR the device
 * credential (PIN / pattern / password)** — so the app works whether or not a fingerprint/face is
 * enrolled. It's usable for a short window after a successful auth; the DB passphrase is
 * encrypted/decrypted immediately after auth, well inside that window (no CryptoObject needed —
 * that path is biometric-only and would fail on PIN-only devices).
 */
object KeyStoreManager {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "ClearStreakMasterKey"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val AUTH_VALIDITY_SECONDS = 30

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    fun getOrCreateMasterKey(): SecretKey {
        if (!keyStore.containsAlias(KEY_ALIAS)) generateMasterKey(useStrongBox = true)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    /** Remove the master key (e.g. after it's permanently invalidated by a lock change). */
    fun deleteMasterKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) keyStore.deleteEntry(KEY_ALIAS)
    }

    private fun generateMasterKey(useStrongBox: Boolean) {
        try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

            val builder = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(true)

            // Accept biometric OR device credential. (We deliberately do NOT invalidate on new
            // biometric enrollment — that's biometric-specific and would nuke a PIN-unlockable vault.)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                builder.setUserAuthenticationParameters(
                    AUTH_VALIDITY_SECONDS,
                    KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                )
            } else {
                @Suppress("DEPRECATION")
                builder.setUserAuthenticationValidityDurationSeconds(AUTH_VALIDITY_SECONDS)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && useStrongBox) {
                builder.setIsStrongBoxBacked(true)
            }

            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        } catch (e: Exception) {
            if (useStrongBox && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && e is StrongBoxUnavailableException) {
                generateMasterKey(useStrongBox = false)
            } else {
                throw e
            }
        }
    }

    /** Encrypt within the auth window. Returns ciphertext + IV. */
    fun encrypt(data: ByteArray): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateMasterKey())
        return cipher.doFinal(data) to cipher.iv
    }

    /** Decrypt within the auth window. */
    fun decrypt(ciphertext: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateMasterKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(ciphertext)
    }
}
