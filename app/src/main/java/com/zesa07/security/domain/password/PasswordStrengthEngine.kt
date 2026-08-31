package com.zesa07.security.domain.password

import com.zesa07.security.domain.model.PasswordAnalysis
import com.zesa07.security.domain.model.PasswordVerdict
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Password-strength tester.
 *
 * IMPORTANT: this operates purely on text the user types into a local text field for
 * *educational testing* - it never checks against, submits to, or retrieves any real account
 * password, and nothing typed here is transmitted off-device or persisted verbatim (see
 * PasswordTesterViewModel - only the *analysis result*, never the raw string, may be logged).
 */
object PasswordStrengthEngine {

    private val COMMON_PASSWORDS = setOf(
        "123456", "password", "123456789", "12345678", "12345", "qwerty", "abc123",
        "password1", "111111", "iloveyou", "admin", "welcome", "monkey", "login",
        "letmein", "dragon", "football", "starwars", "zesa07"
    )

    private val KEYBOARD_RUNS = listOf("qwerty", "asdf", "zxcv", "12345", "09876")

    fun analyze(password: String): PasswordAnalysis {
        val issues = mutableListOf<String>()
        val suggestions = mutableListOf<String>()

        if (password.isEmpty()) {
            return PasswordAnalysis(
                verdict = PasswordVerdict.VERY_WEAK,
                entropyBits = 0.0,
                crackTimeEstimate = "instant",
                issues = listOf("Password is empty"),
                suggestions = listOf("Enter a password to analyze")
            )
        }

        val lower = password.lowercase()
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        var poolSize = 0
        if (hasLower) poolSize += 26
        if (hasUpper) poolSize += 26
        if (hasDigit) poolSize += 10
        if (hasSymbol) poolSize += 32

        var entropy = if (poolSize > 0) password.length * log2(poolSize.toDouble()) else 0.0

        if (password.length < 8) {
            issues += "Shorter than 8 characters"
            suggestions += "Use at least 12 characters"
        }
        if (!hasUpper) { issues += "No uppercase letters"; suggestions += "Add uppercase letters" }
        if (!hasLower) { issues += "No lowercase letters"; suggestions += "Add lowercase letters" }
        if (!hasDigit) { issues += "No digits"; suggestions += "Add digits" }
        if (!hasSymbol) { issues += "No symbols"; suggestions += "Add symbols like !@#$%" }

        if (COMMON_PASSWORDS.any { lower.contains(it) }) {
            issues += "Contains a very common password fragment"
            suggestions += "Avoid common passwords and dictionary words"
            entropy *= 0.25
        }
        if (KEYBOARD_RUNS.any { lower.contains(it) }) {
            issues += "Contains a keyboard-pattern run"
            suggestions += "Avoid keyboard patterns like qwerty or 12345"
            entropy *= 0.5
        }
        if (Regex("(.)\\1{2,}").containsMatchIn(password)) {
            issues += "Contains repeated characters (e.g. aaa, 111)"
            entropy *= 0.7
        }
        if (isSequential(lower)) {
            issues += "Contains a sequential run (e.g. abcd, 1234)"
            entropy *= 0.6
        }

        val verdict = when {
            entropy < 28 -> PasswordVerdict.VERY_WEAK
            entropy < 36 -> PasswordVerdict.WEAK
            entropy < 60 -> PasswordVerdict.FAIR
            entropy < 80 -> PasswordVerdict.STRONG
            else -> PasswordVerdict.VERY_STRONG
        }

        if (suggestions.isEmpty()) suggestions += "Great password - consider a password manager to keep it unique per site"

        return PasswordAnalysis(
            verdict = verdict,
            entropyBits = entropy,
            crackTimeEstimate = estimateCrackTime(entropy),
            issues = issues,
            suggestions = suggestions
        )
    }

    /** Generates a strong local test password - never a real credential. */
    fun generateTestPassword(length: Int = 16): String {
        val charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#\$%^&*()-_=+"
        val random = java.security.SecureRandom()
        return (1..length).map { charset[random.nextInt(charset.length)] }.joinToString("")
    }

    private fun isSequential(s: String): Boolean {
        var run = 1
        for (i in 1 until s.length) {
            if (s[i].code == s[i - 1].code + 1) {
                run++
                if (run >= 4) return true
            } else run = 1
        }
        return false
    }

    /** Assumes an offline attack at 10 billion guesses/sec (modern GPU rig, conservative-high). */
    private fun estimateCrackTime(entropyBits: Double): String {
        val guessesPerSecond = 1e10
        val combinations = 2.0.pow(entropyBits)
        val seconds = combinations / guessesPerSecond
        return when {
            seconds < 1 -> "instant"
            seconds < 60 -> "${seconds.roundToLong()} seconds"
            seconds < 3600 -> "${(seconds / 60).roundToLong()} minutes"
            seconds < 86400 -> "${(seconds / 3600).roundToLong()} hours"
            seconds < 31_536_000 -> "${(seconds / 86400).roundToLong()} days"
            seconds < 31_536_000_000.0 -> "${(seconds / 31_536_000).roundToLong()} years"
            else -> "centuries"
        }
    }
}
