package com.zesa07.security.domain.model

/** Static educational risk catalog for common Android permissions (not a live device audit of other apps). */
object PermissionCatalog {

    private val catalog: Map<String, Pair<RiskLevel, String>> = mapOf(
        "android.permission.CAMERA" to (RiskLevel.HIGH to
            "Allows capturing photos/video. Malicious apps could covertly surveil users if this is granted without a clear in-app reason."),
        "android.permission.RECORD_AUDIO" to (RiskLevel.HIGH to
            "Allows recording audio. Review whether the requesting feature genuinely needs live audio access."),
        "android.permission.ACCESS_FINE_LOCATION" to (RiskLevel.HIGH to
            "Precise GPS location can reveal a user's exact movements; required by Android for Wi-Fi scan results."),
        "android.permission.ACCESS_COARSE_LOCATION" to (RiskLevel.MEDIUM to
            "Approximate location (network-based); lower precision than fine location but still sensitive."),
        "android.permission.READ_CONTACTS" to (RiskLevel.HIGH to
            "Grants access to the user's entire address book - a common vector for spam/social-engineering campaigns."),
        "android.permission.READ_SMS" to (RiskLevel.CRITICAL to
            "SMS often carries 2FA codes; this permission is a major target for banking-trojan malware."),
        "android.permission.SEND_SMS" to (RiskLevel.CRITICAL to
            "Can be abused to silently send premium-rate SMS or spread smishing links."),
        "android.permission.READ_EXTERNAL_STORAGE" to (RiskLevel.MEDIUM to
            "Broad file access; scoped storage on modern Android reduces but doesn't eliminate risk."),
        "android.permission.SYSTEM_ALERT_WINDOW" to (RiskLevel.CRITICAL to
            "Allows drawing over other apps - used by real overlay/phishing malware to fake login screens."),
        "android.permission.BIND_ACCESSIBILITY_SERVICE" to (RiskLevel.CRITICAL to
            "One of the most abused permissions in Android malware; can read screen content and inject input across apps."),
        "android.permission.REQUEST_INSTALL_PACKAGES" to (RiskLevel.HIGH to
            "Lets an app prompt installation of other APKs - a common malware-dropper technique."),
        "android.permission.BLUETOOTH_CONNECT" to (RiskLevel.MEDIUM to
            "Allows connecting to already-paired Bluetooth devices."),
        "android.permission.BLUETOOTH_SCAN" to (RiskLevel.MEDIUM to
            "Allows discovering nearby Bluetooth devices; can be used for proximity tracking if combined with other data."),
        "android.permission.POST_NOTIFICATIONS" to (RiskLevel.LOW to
            "Allows showing notifications; low risk on its own."),
        "android.permission.INTERNET" to (RiskLevel.LOW to
            "Required for any network access; risk depends entirely on what data is sent and to where.")
    )

    fun explain(permission: String): Pair<RiskLevel, String> =
        catalog[permission] ?: (RiskLevel.LOW to "No specific guidance available for this permission; review its official Android documentation.")

    fun all(): Map<String, Pair<RiskLevel, String>> = catalog
}
