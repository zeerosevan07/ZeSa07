package com.zesa07.security.domain.network

import com.zesa07.security.domain.model.LabPortResult
import com.zesa07.security.util.IpUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Checks TCP connect-ability of a small, fixed, well-known-service port list against a
 * user-authorized local-lab target only. This is a plain TCP connect probe (the "vanilla"
 * educational technique) - no SYN/stealth scanning, no raw sockets, no spoofed packets, no OS
 * fingerprinting exploitation. Intended to teach *why* exposed services are risky and how
 * defenders see connection attempts in logs.
 */
@Singleton
class LabPortScanner @Inject constructor() {

    // A deliberately small, well-known educational port set - not a large NSE-style sweep.
    val commonPorts: List<Pair<Int, String>> = listOf(
        21 to "FTP", 22 to "SSH", 23 to "Telnet", 25 to "SMTP", 53 to "DNS",
        80 to "HTTP", 110 to "POP3", 139 to "NetBIOS", 143 to "IMAP",
        443 to "HTTPS", 445 to "SMB", 3306 to "MySQL", 3389 to "RDP",
        5432 to "PostgreSQL", 6379 to "Redis", 8080 to "HTTP-Alt", 8443 to "HTTPS-Alt"
    )

    suspend fun scanPorts(
        ip: String,
        timeoutMs: Int = 500,
        ports: List<Pair<Int, String>> = commonPorts
    ): List<LabPortResult> {
        require(IpUtils.isAuthorizedLabAddress(ip)) {
            "Refusing to port-scan '$ip': ZeSa07 only probes ports on your own authorized " +
                "local-lab devices (private/loopback addresses)."
        }
        return withContext(Dispatchers.IO) {
            ports.map { (port, name) ->
                async { checkPort(ip, port, name, timeoutMs) }
            }.awaitAll()
        }
    }

    private fun checkPort(ip: String, port: Int, name: String, timeoutMs: Int): LabPortResult {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
                LabPortResult(port = port, serviceName = name, open = true)
            }
        } catch (_: Exception) {
            LabPortResult(port = port, serviceName = name, open = false)
        }
    }
}
