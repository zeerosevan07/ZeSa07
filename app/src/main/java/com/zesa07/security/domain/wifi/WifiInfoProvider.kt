package com.zesa07.security.domain.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import com.zesa07.security.domain.model.WifiNetworkInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads Wi-Fi metadata using only standard public Android APIs ([WifiManager]) - the same
 * information any app with location + Wi-Fi permission can see. This module does NOT:
 *  - attempt to associate with, deauthenticate, or send frames to any network
 *  - crack or brute-force any Wi-Fi password / PSK / handshake
 *  - capture or analyze packets
 * It is a read-only educational viewer for signal strength, band, and reported security type,
 * used to teach concepts like open-network risk, WEP/WPA2/WPA3 differences, and rogue-AP
 * awareness.
 */
@Singleton
class WifiInfoProvider @Inject constructor(@ApplicationContext private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    /** Requires ACCESS_FINE_LOCATION (granted at runtime) for scan results per Android policy. */
    @SuppressLint("MissingPermission")
    fun getScanResults(): List<WifiNetworkInfo> {
        val currentBssid = wifiManager.connectionInfo?.bssid
        return wifiManager.scanResults.map { r ->
            WifiNetworkInfo(
                ssid = r.SSID.ifBlank { "(hidden network)" },
                bssid = r.BSSID ?: "unknown",
                signalDbm = r.level,
                frequencyMhz = r.frequency,
                security = classifyCapabilities(r.capabilities),
                isCurrentConnection = r.BSSID == currentBssid
            )
        }
    }

    fun requestScan(): Boolean = wifiManager.startScan()

    fun isWifiEnabled(): Boolean = wifiManager.isWifiEnabled

    private fun classifyCapabilities(capabilities: String): String = when {
        capabilities.contains("WPA3") -> "WPA3"
        capabilities.contains("WPA2") -> "WPA2"
        capabilities.contains("WPA") -> "WPA"
        capabilities.contains("WEP") -> "WEP (insecure)"
        capabilities.isBlank() || capabilities.contains("ESS") && !capabilities.contains("WPA") -> "Open (no encryption)"
        else -> "Unknown"
    }

    /** Simple, non-judgmental education notes shown alongside a network's security type. */
    fun securityAdvice(security: String): String = when {
        security.startsWith("Open") -> "Open networks send traffic unencrypted at the Wi-Fi layer. Avoid logging into sensitive accounts, or use a trusted VPN."
        security.startsWith("WEP") -> "WEP is cryptographically broken and can be cracked in minutes with public tools. Treat as equivalent to an open network."
        security == "WPA" -> "Legacy WPA (TKIP) has known weaknesses. Prefer WPA2/WPA3 networks when available."
        security == "WPA2" -> "WPA2 is broadly secure when using a strong passphrase, but is vulnerable to offline dictionary attacks against weak passphrases (e.g. KRACK-class issues on old firmware)."
        security == "WPA3" -> "WPA3 adds protections against offline dictionary attacks (SAE) and is currently the strongest consumer Wi-Fi standard."
        else -> "Security type could not be determined from the beacon capabilities."
    }
}
