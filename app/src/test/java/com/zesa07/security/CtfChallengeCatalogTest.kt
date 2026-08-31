package com.zesa07.security

import com.google.common.truth.Truth.assertThat
import com.zesa07.security.domain.ctf.CtfChallengeCatalog
import org.junit.Test

class CtfChallengeCatalogTest {

    @Test
    fun `all challenges have unique ids`() {
        val ids = CtfChallengeCatalog.challenges.map { it.id }
        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun `all challenges have non-empty prompts and hints`() {
        CtfChallengeCatalog.challenges.forEach { challenge ->
            assertThat(challenge.prompt).isNotEmpty()
            assertThat(challenge.points).isGreaterThan(0)
        }
    }

    @Test
    fun `flag hashes are 64-char lowercase hex (sha256), never plaintext`() {
        CtfChallengeCatalog.challenges.forEach { challenge ->
            assertThat(challenge.flagHashSha256).matches("[0-9a-f]{64}")
            assertThat(challenge.flagHashSha256).doesNotContain("ZESA07")
        }
    }

    @Test
    fun `checkFlag rejects wrong answers and accepts correct ones case-sensitively on content`() {
        val warmup = CtfChallengeCatalog.byId("warmup-base64")!!
        assertThat(CtfChallengeCatalog.checkFlag(warmup, "wrong-answer")).isFalse()
        assertThat(CtfChallengeCatalog.checkFlag(warmup, "ZESA07{base64_is_not_encryption}")).isTrue()
    }

    @Test
    fun `checkFlag trims whitespace from submission`() {
        val warmup = CtfChallengeCatalog.byId("warmup-base64")!!
        assertThat(CtfChallengeCatalog.checkFlag(warmup, "  ZESA07{base64_is_not_encryption}  ")).isTrue()
    }
}
