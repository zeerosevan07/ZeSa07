package com.zesa07.security

import com.google.common.truth.Truth.assertThat
import com.zesa07.security.domain.network.LabPortScanner
import com.zesa07.security.domain.network.LocalNetworkScanner
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Verifies the core safety contract: offensive networking features refuse to operate against
 * any non-private-range target, regardless of how they're invoked.
 */
class NetworkScannerSafetyTest {

    @Test
    fun `LocalNetworkScanner refuses public subnet bases`() = runTest {
        val scanner = LocalNetworkScanner()
        try {
            scanner.discoverDevices("8.8.8")
            throw AssertionError("Expected IllegalArgumentException for public subnet")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("Refusing to scan")
        }
    }

    @Test
    fun `LocalNetworkScanner refuses public single-host probes`() = runTest {
        val scanner = LocalNetworkScanner()
        try {
            scanner.probeSingle("1.1.1.1")
            throw AssertionError("Expected IllegalArgumentException for public IP")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("Refusing to probe")
        }
    }

    @Test
    fun `LocalNetworkScanner accepts a well-formed private subnet without throwing validation error`() = runTest {
        val scanner = LocalNetworkScanner()
        // We only assert it does not throw the *validation* IllegalArgumentException; actual
        // network reachability in a unit-test sandbox is irrelevant/unavailable here.
        try {
            scanner.discoverDevices("192.168.1", timeoutMs = 1)
        } catch (e: IllegalArgumentException) {
            throw AssertionError("Private subnet should not be rejected by the safety gate", e)
        } catch (_: Exception) {
            // Any other exception (e.g. network unavailable in CI) is acceptable here.
        }
    }

    @Test
    fun `LabPortScanner refuses public IPs`() = runTest {
        val scanner = LabPortScanner()
        try {
            scanner.scanPorts("93.184.216.34")
            throw AssertionError("Expected IllegalArgumentException for public IP")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).contains("Refusing to port-scan")
        }
    }

    @Test
    fun `LabPortScanner common port list is small and well-known, not an exhaustive sweep`() {
        val scanner = LabPortScanner()
        assertThat(scanner.commonPorts.size).isAtMost(25)
        assertThat(scanner.commonPorts.map { it.first }).containsNoDuplicates()
    }
}
