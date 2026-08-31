package com.zesa07.security.domain.model

/** A network discovered on a scan (simulated or, for the current connection, real metadata). */
data class WifiNetworkInfo(
    val ssid: String,
    val bssid: String,
    val signalDbm: Int,
    val frequencyMhz: Int,
    val security: String,
    val isCurrentConnection: Boolean = false
)

/** A device found during authorized local-lab discovery. */
data class LabDevice(
    val ipAddress: String,
    val hostname: String?,
    val macPrefixVendor: String?,
    val reachable: Boolean,
    val roundTripMs: Long?
)

/** Result of an authorized-lab port/service probe. */
data class LabPortResult(
    val port: Int,
    val serviceName: String,
    val open: Boolean,
    val banner: String? = null
)

enum class PasswordVerdict { VERY_WEAK, WEAK, FAIR, STRONG, VERY_STRONG }

data class PasswordAnalysis(
    val verdict: PasswordVerdict,
    val entropyBits: Double,
    val crackTimeEstimate: String,
    val issues: List<String>,
    val suggestions: List<String>
)

enum class HashAlgorithm { MD5, SHA1, SHA256, SHA512 }

data class PermissionFinding(
    val permission: String,
    val riskLevel: RiskLevel,
    val explanation: String
)

enum class RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

/** A single scripted, harmless step in the malware-behavior simulator timeline. */
data class SimBehaviorStep(
    val id: Int,
    val title: String,
    val mitreAttckId: String?,
    val description: String,
    val defensiveTip: String
)

data class CtfChallenge(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val points: Int,
    val prompt: String,
    val flagHashSha256: String, // flag never stored in plaintext
    val hints: List<String>
)

data class LabExercise(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val objective: String,
    val steps: List<String>,
    val vulnerabilityExplained: String,
    val remediation: String
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val unlocked: Boolean,
    val progress: Float
)
