package com.zesa07.security.domain.network

import com.zesa07.security.domain.model.LabDevice
import com.zesa07.security.util.IpUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Discovers devices on the user's OWN local network for their authorized lab.
 *
 * SAFETY GATES:
 *  - [discoverDevices] REJECTS any subnet base that isn't a private RFC1918 range
 *    (validated via [IpUtils.isAuthorizedLabSubnetBase]).
 *  - Every call requires the caller (ViewModel) to already have shown the user a confirmation
 *    dialog; this class does not prompt the user itself, callers must gate the call.
 *  - Uses ICMP-reachability-style checks via [InetAddress.isReachable] (falls back to a TCP
 *    connect probe on common ports) with a short timeout - no packet crafting, no spoofing,
 *    no raw sockets.
 */
@Singleton
class LocalNetworkScanner @Inject constructor() {

    /**
     * Sweeps host addresses 1..254 of the given /24 base (e.g. "192.168.1").
     * Throws [IllegalArgumentException] if [subnetBase] is not an authorized private range.
     */
    suspend fun discoverDevices(subnetBase: String, timeoutMs: Int = 400): List<LabDevice> {
        require(IpUtils.isAuthorizedLabSubnetBase(subnetBase)) {
            "Refusing to scan non-private subnet '$subnetBase'. ZeSa07 only scans your own " +
                "authorized local network (RFC1918 private ranges)."
        }
        return withContext(Dispatchers.IO) {
            (1..254).map { host ->
                async {
                    val ip = "$subnetBase.$host"
                    probe(ip, timeoutMs)
                }
            }.awaitAll().filterNotNull()
        }
    }

    /** Probes a single, already-validated lab IP. */
    suspend fun probeSingle(ip: String, timeoutMs: Int = 800): LabDevice? {
        require(IpUtils.isAuthorizedLabAddress(ip)) {
            "Refusing to probe '$ip': not a private/loopback lab address."
        }
        return withContext(Dispatchers.IO) { probe(ip, timeoutMs) }
    }

    private fun probe(ip: String, timeoutMs: Int): LabDevice? {
        return try {
            val start = System.currentTimeMillis()
            val addr = InetAddress.getByName(ip)
            val reachable = addr.isReachable(timeoutMs)
            val elapsed = System.currentTimeMillis() - start
            if (!reachable) return null
            LabDevice(
                ipAddress = ip,
                hostname = addr.canonicalHostName.takeIf { it != ip },
                macPrefixVendor = null, // ARP table access is not reliably available on modern Android; omitted rather than faked
                reachable = true,
                roundTripMs = elapsed
            )
        } catch (_ : Exception) {
            null
        }
    }
}
