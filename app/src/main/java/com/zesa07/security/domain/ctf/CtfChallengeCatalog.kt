package com.zesa07.security.domain.ctf

import com.zesa07.security.domain.crypto.HashingEngine
import com.zesa07.security.domain.model.CtfChallenge
import com.zesa07.security.domain.model.HashAlgorithm

/**
 * All challenges are entirely self-contained puzzles bundled with the app (encoding tricks,
 * crypto riddles, log-analysis exercises). None require or teach attacking a real external
 * system. Flags are checked via SHA-256 comparison so the plaintext flag is never stored
 * and never printed in logs.
 */
object CtfChallengeCatalog {

    private fun flag(plaintext: String) = HashingEngine.hash(plaintext, HashAlgorithm.SHA256)

    val challenges: List<CtfChallenge> = listOf(
        CtfChallenge(
            id = "warmup-base64",
            title = "Warm-Up: Encoded Message",
            category = "Encoding",
            difficulty = "Beginner",
            points = 50,
            prompt = "Decode this Base64 string to find the flag: WkVTQTA3e2Jhc2U2NF9pc19ub3RfZW5jcnlwdGlvbn0=",
            flagHashSha256 = flag("ZESA07{base64_is_not_encryption}"),
            hints = listOf("Base64 is encoding, not encryption - it's fully reversible.", "Try an online or CLI base64 decoder.")
        ),
        CtfChallenge(
            id = "caesar-cipher",
            title = "Ancient Rotation",
            category = "Cryptography",
            difficulty = "Beginner",
            points = 75,
            prompt = "This flag was shifted by 13 (ROT13): MRFN07{png_naq_zbhfr_ner_bcgvbany}",
            flagHashSha256 = flag("ZESA07{cat_and_mouse_are_optional}"),
            hints = listOf("ROT13 is symmetric - applying it twice returns the original text.")
        ),
        CtfChallenge(
            id = "hash-id",
            title = "Identify the Hash",
            category = "Cryptography",
            difficulty = "Beginner",
            points = 75,
            prompt = "The hash 5f4dcc3b5aa765d61d8327deb882cf99 is a hash of a very common password, " +
                "run using the algorithm whose output is exactly 32 hex characters. Name that algorithm, " +
                "lowercase, and submit as ZESA07{algorithm}.",
            flagHashSha256 = flag("ZESA07{md5}"),
            hints = listOf("32 hex chars = 128 bits of output.", "This is the weakest widely-known general-purpose hash function.")
        ),
        CtfChallenge(
            id = "log-hunt",
            title = "Suspicious Login Log",
            category = "Blue Team / Log Analysis",
            difficulty = "Intermediate",
            points = 100,
            prompt = "In the Security Logs screen's sample dataset, one authorized-lab port scan " +
                "touched a port associated with unencrypted remote shell access. Name the port number " +
                "and submit as ZESA07{port_number}.",
            flagHashSha256 = flag("ZESA07{23}"),
            hints = listOf("Telnet is unencrypted.", "Check the Lab Port Scanner's default port list.")
        ),
        CtfChallenge(
            id = "entropy-math",
            title = "Entropy Estimate",
            category = "Password Security",
            difficulty = "Intermediate",
            points = 100,
            prompt = "A password uses only lowercase letters and is 8 characters long. Approximately " +
                "how many bits of entropy does it have? Round to the nearest whole number and submit as ZESA07{N}.",
            flagHashSha256 = flag("ZESA07{38}"),
            hints = listOf("Entropy ≈ length × log2(pool size).", "log2(26) ≈ 4.7; 8 × 4.7 ≈ 37.6")
        ),
        CtfChallenge(
            id = "permission-danger",
            title = "The Overlay Threat",
            category = "Android Security",
            difficulty = "Advanced",
            points = 150,
            prompt = "Which single Android permission is most associated with fake-login overlay " +
                "attacks on top of legitimate banking apps? Answer as ZESA07{PERMISSION_CONSTANT} " +
                "using the exact Android manifest constant, e.g. ZESA07{SYSTEM_ALERT_WINDOW}.",
            flagHashSha256 = flag("ZESA07{SYSTEM_ALERT_WINDOW}"),
            hints = listOf("Check the Permission Analyzer screen's risk catalog.", "It lets an app draw over other apps.")
        )
    )

    fun byId(id: String): CtfChallenge? = challenges.find { it.id == id }

    fun checkFlag(challenge: CtfChallenge, submitted: String): Boolean =
        HashingEngine.hash(submitted.trim(), HashAlgorithm.SHA256).equals(challenge.flagHashSha256, ignoreCase = true)
}
