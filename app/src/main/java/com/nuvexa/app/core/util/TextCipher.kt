package com.nuvexa.app.core.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Simple password-based text encryption: PBKDF2 (120k iterations, SHA-256) derives an
 * AES-256 key from the password and a random salt; AES/GCM provides authenticated
 * encryption. Salt + IV + ciphertext are packed together and Base64-encoded, so the
 * result is self-contained and only needs the password to decrypt.
 */
object TextCipher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12
    private const val GCM_TAG_LENGTH_BITS = 128

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plainText: String, password: String): Result<String> = runCatching {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH).also { random.nextBytes(it) }
        val iv = ByteArray(IV_LENGTH).also { random.nextBytes(it) }
        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val packed = salt + iv + cipherText
        Base64.encodeToString(packed, Base64.NO_WRAP)
    }

    fun decrypt(encoded: String, password: String): Result<String> = runCatching {
        val packed = Base64.decode(encoded, Base64.NO_WRAP)
        require(packed.size > SALT_LENGTH + IV_LENGTH) { "Ciphertext too short" }
        val salt = packed.copyOfRange(0, SALT_LENGTH)
        val iv = packed.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
        val cipherText = packed.copyOfRange(SALT_LENGTH + IV_LENGTH, packed.size)
        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        String(cipher.doFinal(cipherText), Charsets.UTF_8)
    }
}
