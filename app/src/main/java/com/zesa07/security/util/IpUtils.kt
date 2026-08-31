package com.zesa07.security.util

/**
 * Central safety gate for every "offensive" networking feature in the app.
 *
 * Every scan/discovery/port-check routine MUST pass its target through
 * [isAuthorizedLabAddress] first and refuse to proceed if it returns false. This keeps the app
 * confined to the user's own local network (RFC1918 private ranges + loopback), which is the
 * only environment a user can plausibly self-authorize inside a mobile app with no way to
 * verify real-world ownership documents.
 */
object IpUtils {

    private val PRIVATE_V4_PATTERNS = listOf(
        Regex("""^10\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$"""),
        Regex("""^192\.168\.(\d{1,3})\.(\d{1,3})$"""),
        Regex("""^172\.(1[6-9]|2\d|3[01])\.(\d{1,3})\.(\d{1,3})$"""),
        Regex("""^127\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$"""),
        Regex("""^169\.254\.(\d{1,3})\.(\d{1,3})$""") // link-local
    )

    private val OCTET_RANGE = 0..255

    /** True only for loopback / RFC1918 / link-local IPv4 addresses with valid octets. */
    fun isAuthorizedLabAddress(ip: String): Boolean {
        val trimmed = ip.trim()
        if (trimmed == "localhost") return true
        if (!PRIVATE_V4_PATTERNS.any { it.matches(trimmed) }) return false
        val octets = trimmed.split(".").mapNotNull { it.toIntOrNull() }
        if (octets.size != 4) return false
        return octets.all { it in OCTET_RANGE }
    }

    /** Validates a CIDR-less base like "192.168.1" used to derive a /24 sweep range. */
    fun isAuthorizedLabSubnetBase(base: String): Boolean {
        val candidate = "$base.1"
        return isAuthorizedLabAddress(candidate)
    }

    fun isValidIpv4(ip: String): Boolean {
        val parts = ip.trim().split(".")
        if (parts.size != 4) return false
        return parts.all { p -> p.toIntOrNull()?.let { it in OCTET_RANGE } == true }
    }
}
