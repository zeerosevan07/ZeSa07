package com.zesa07.security.domain.ctf

import com.zesa07.security.domain.model.LabExercise

/**
 * Each exercise walks through a *conceptual* vulnerable-application pattern (e.g. SQL injection,
 * insecure storage) using an in-app simulated mini-app or narrated scenario - never a real
 * exploit against a live third-party target. Where a hands-on component exists, it runs against
 * a bundled local simulated service (see ui/screens/labs for the interactive versions).
 */
object LabExerciseCatalog {
    val exercises: List<LabExercise> = listOf(
        LabExercise(
            id = "sim-sqli",
            title = "SQL Injection: Simulated Login Bypass",
            category = "Web / OWASP",
            summary = "Explore how a naive login query can be bypassed using classic SQL " +
                "injection syntax, against an in-app SIMULATED login form (no real database).",
            objective = "Understand why string-concatenated SQL queries are dangerous and how " +
                "parameterized queries fix it.",
            steps = listOf(
                "Open the simulated login form in this lab.",
                "Try the username admin' -- and any password.",
                "Observe the simulated backend explain why the comment token truncates the query.",
                "Toggle 'Use parameterized query' and try the same input again to see it fail safely."
            ),
            vulnerabilityExplained = "The vulnerable query builds SQL by concatenating raw user " +
                "input: SELECT * FROM users WHERE user='<input>' AND pass='...'. Injecting a " +
                "single quote plus a comment token changes the query's logic.",
            remediation = "Always use parameterized queries / prepared statements, apply least-" +
                "privilege database accounts, and validate input server-side."
        ),
        LabExercise(
            id = "sim-insecure-storage",
            title = "Insecure Local Storage",
            category = "Mobile / OWASP MASVS",
            summary = "See why storing secrets in plaintext SharedPreferences is risky, using a " +
                "simulated 'vulnerable notes app' bundled in this lab.",
            objective = "Learn the difference between plaintext storage and Android Keystore-backed " +
                "encrypted storage.",
            steps = listOf(
                "Save a note in the simulated 'vulnerable' storage mode.",
                "Inspect the simulated raw storage viewer to see the note in plaintext.",
                "Switch to 'secure' mode (backed by EncryptedSharedPreferences) and repeat.",
                "Compare what the raw storage viewer shows in each mode."
            ),
            vulnerabilityExplained = "Data written with plain SharedPreferences is stored as " +
                "unencrypted XML in app-private storage - readable if the device is rooted or " +
                "the backup is extracted.",
            remediation = "Use EncryptedSharedPreferences or the Android Keystore system for any " +
                "sensitive local data, and disable auto-backup for sensitive files."
        ),
        LabExercise(
            id = "sim-weak-crypto",
            title = "Weak Cryptography Detection",
            category = "Cryptography",
            summary = "Compare MD5/SHA1 vs SHA-256/bcrypt-style salted hashing for password storage " +
                "using the built-in Hashing Toolkit.",
            objective = "Recognize outdated hashing choices in code review.",
            steps = listOf(
                "Open the Hashing Toolkit and hash the same test string with MD5 and SHA-256.",
                "Note there is no built-in 'work factor' in either - discuss why raw hashes are " +
                    "unsuitable for password storage even when using SHA-256.",
                "Read the in-app note on why bcrypt/scrypt/Argon2 (deliberately slow, salted) are " +
                    "the correct choice for password storage."
            ),
            vulnerabilityExplained = "Fast general-purpose hashes (MD5/SHA1/SHA256) let attackers " +
                "test billions of guesses per second offline. Password-storage hashing needs to " +
                "be deliberately slow and salted.",
            remediation = "Use bcrypt, scrypt, or Argon2 with a unique salt per password for any " +
                "real authentication system; never roll your own scheme."
        ),
        LabExercise(
            id = "sim-open-port-risk",
            title = "Exposed Service Risk Review",
            category = "Network",
            summary = "Run the authorized Lab Port Scanner against a device you own (e.g. a home " +
                "router or a Raspberry Pi you control) and review what each open port implies.",
            objective = "Translate a port scan result into a concrete risk-reduction action plan.",
            steps = listOf(
                "Confirm you own or are authorized to test the target device.",
                "Run the Lab Port Scanner against its private IP address.",
                "For each open port, read the in-app explanation of that service's typical risk.",
                "Write down one remediation action per open, unnecessary service (e.g. disable Telnet, enable SSH key auth)."
            ),
            vulnerabilityExplained = "Every open network service is additional attack surface. " +
                "Legacy/unencrypted services (Telnet, unauthenticated Redis, old SMB) are " +
                "especially high risk on a home or lab network.",
            remediation = "Disable unused services, prefer encrypted alternatives (SSH over Telnet, " +
                "HTTPS over HTTP), and place management interfaces behind a VPN or firewall rule."
        )
    )

    fun byId(id: String): LabExercise? = exercises.find { it.id == id }
}
