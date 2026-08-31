package com.zesa07.security.domain.crypto

import com.zesa07.security.domain.model.HashAlgorithm
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Educational hashing & symmetric-crypto toolkit. Pure local computation - no network calls,
 * no key material ever leaves the process, nothing is persisted unless the user explicitly
 * saves a result to the scan log.
 */
object HashingEngine {

    fun hash(input: String, algorithm: HashAlgorithm): String {
        val digest = MessageDigest.getInstance(
            when (algorithm) {
                HashAlgorithm.MD5 -> "MD5"
                HashAlgorithm.SHA1 -> "SHA-1"
                HashAlgorithm.SHA256 -> "SHA-256"
                HashAlgorithm.SHA512 -> "SHA-512"
            }
        )
        return digest.digest(input.toByteArray(Charsets.UTF_8)).toHex()
    }

    fun hashAll(input: String): Map<HashAlgorithm, String> =
        HashAlgorithm.entries.associateWith { hash(input, it) }

    /** Simple educational demo of salting: shows why identical passwords hash differently. */
    fun saltedSha256(input: String, salt: String): String = hash(salt + input, HashAlgorithm.SHA256)

    fun randomSalt(bytes: Int = 16): String {
        val b = ByteArray(bytes)
        SecureRandom().nextBytes(b)
        return b.toHex()
    }

    /** AES-256-GCM round trip demo, entirely local, key generated fresh each call. */
    fun aesGcmDemo(plaintext: String): AesDemoResult {
        val keyGen = KeyGenerator.getInstance("AES").apply { init(256) }
        val key = keyGen.generateKey()
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val cipherBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        val decrypted = String(decryptCipher.doFinal(cipherBytes), Charsets.UTF_8)

        return AesDemoResult(
            keyHex = key.encoded.toHex(),
            ivHex = iv.toHex(),
            ciphertextHex = cipherBytes.toHex(),
            decryptedRoundTrip = decrypted
        )
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
}

data class AesDemoResult(
    val keyHex: String,
    val ivHex: String,
    val ciphertextHex: String,
    val decryptedRoundTrip: String
)
