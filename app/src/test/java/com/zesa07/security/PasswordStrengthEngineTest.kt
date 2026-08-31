package com.zesa07.security

import com.google.common.truth.Truth.assertThat
import com.zesa07.security.domain.model.PasswordVerdict
import com.zesa07.security.domain.password.PasswordStrengthEngine
import org.junit.Test

class PasswordStrengthEngineTest {

    @Test
    fun `empty password is very weak with zero entropy`() {
        val result = PasswordStrengthEngine.analyze("")
        assertThat(result.verdict).isEqualTo(PasswordVerdict.VERY_WEAK)
        assertThat(result.entropyBits).isEqualTo(0.0)
    }

    @Test
    fun `common password is flagged very weak`() {
        val result = PasswordStrengthEngine.analyze("password1")
        assertThat(result.verdict).isEqualTo(PasswordVerdict.VERY_WEAK)
        assertThat(result.issues).isNotEmpty()
    }

    @Test
    fun `keyboard pattern is flagged`() {
        val result = PasswordStrengthEngine.analyze("qwerty123")
        assertThat(result.issues.any { it.contains("keyboard", ignoreCase = true) }).isTrue()
    }

    @Test
    fun `long random mixed-case password with symbols is strong`() {
        val result = PasswordStrengthEngine.analyze("xQ7\$mZ9!vL2#pR4@")
        assertThat(result.verdict).isAnyOf(PasswordVerdict.STRONG, PasswordVerdict.VERY_STRONG)
    }

    @Test
    fun `repeated characters reduce entropy relative to non-repeated equivalent`() {
        val repeated = PasswordStrengthEngine.analyze("aaaaaaaaaaaa")
        val varied = PasswordStrengthEngine.analyze("kTmXbPqLzWnR")
        assertThat(repeated.entropyBits).isLessThan(varied.entropyBits)
    }

    @Test
    fun `generated test password is 16 chars and analyzes as strong`() {
        val generated = PasswordStrengthEngine.generateTestPassword()
        assertThat(generated.length).isEqualTo(16)
        val result = PasswordStrengthEngine.analyze(generated)
        assertThat(result.verdict).isAnyOf(PasswordVerdict.STRONG, PasswordVerdict.VERY_STRONG)
    }

    @Test
    fun `sequential characters are flagged`() {
        val result = PasswordStrengthEngine.analyze("abcd1234EFGH")
        assertThat(result.issues.any { it.contains("sequential", ignoreCase = true) }).isTrue()
    }

    @Test
    fun `missing character classes are each reported`() {
        val result = PasswordStrengthEngine.analyze("alllowercase")
        assertThat(result.issues).contains("No uppercase letters")
        assertThat(result.issues).contains("No digits")
        assertThat(result.issues).contains("No symbols")
    }
}
