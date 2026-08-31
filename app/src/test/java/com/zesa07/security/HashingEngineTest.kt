package com.zesa07.security

import com.google.common.truth.Truth.assertThat
import com.zesa07.security.domain.crypto.HashingEngine
import com.zesa07.security.domain.model.HashAlgorithm
import org.junit.Test

class HashingEngineTest {

    @Test
    fun `md5 produces known digest for empty string`() {
        assertThat(HashingEngine.hash("", HashAlgorithm.MD5)).isEqualTo("d41d8cd98f00b204e9800998ecf8427e")
    }

    @Test
    fun `sha256 known vector for abc`() {
        val expected = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        assertThat(HashingEngine.hash("abc", HashAlgorithm.SHA256)).isEqualTo(expected)
    }

    @Test
    fun `hashAll returns all four algorithms`() {
        val results = HashingEngine.hashAll("test")
        assertThat(results.keys).containsExactly(HashAlgorithm.MD5, HashAlgorithm.SHA1, HashAlgorithm.SHA256, HashAlgorithm.SHA512)
        results.values.forEach { assertThat(it).isNotEmpty() }
    }

    @Test
    fun `same input with different salts produces different hashes`() {
        val h1 = HashingEngine.saltedSha256("password", "saltA")
        val h2 = HashingEngine.saltedSha256("password", "saltB")
        assertThat(h1).isNotEqualTo(h2)
    }

    @Test
    fun `aes gcm demo round trip recovers original plaintext`() {
        val plaintext = "ZeSa07 educational secret"
        val result = HashingEngine.aesGcmDemo(plaintext)
        assertThat(result.decryptedRoundTrip).isEqualTo(plaintext)
        assertThat(result.ciphertextHex).isNotEmpty()
    }

    @Test
    fun `random salts are not trivially identical across calls`() {
        val s1 = HashingEngine.randomSalt()
        val s2 = HashingEngine.randomSalt()
        assertThat(s1).isNotEqualTo(s2)
    }
}
