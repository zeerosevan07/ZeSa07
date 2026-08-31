package com.zesa07.security

import com.google.common.truth.Truth.assertThat
import com.zesa07.security.util.IpUtils
import org.junit.Test

class IpUtilsTest {

    @Test
    fun `private class C addresses are authorized`() {
        assertThat(IpUtils.isAuthorizedLabAddress("192.168.1.1")).isTrue()
        assertThat(IpUtils.isAuthorizedLabAddress("192.168.0.254")).isTrue()
    }

    @Test
    fun `private class A addresses are authorized`() {
        assertThat(IpUtils.isAuthorizedLabAddress("10.0.0.1")).isTrue()
        assertThat(IpUtils.isAuthorizedLabAddress("10.255.255.255")).isTrue()
    }

    @Test
    fun `private class B range is authorized only within 16 to 31`() {
        assertThat(IpUtils.isAuthorizedLabAddress("172.16.0.1")).isTrue()
        assertThat(IpUtils.isAuthorizedLabAddress("172.31.255.255")).isTrue()
        assertThat(IpUtils.isAuthorizedLabAddress("172.32.0.1")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("172.15.255.255")).isFalse()
    }

    @Test
    fun `loopback is authorized`() {
        assertThat(IpUtils.isAuthorizedLabAddress("127.0.0.1")).isTrue()
        assertThat(IpUtils.isAuthorizedLabAddress("localhost")).isTrue()
    }

    @Test
    fun `link-local is authorized`() {
        assertThat(IpUtils.isAuthorizedLabAddress("169.254.1.1")).isTrue()
    }

    @Test
    fun `public internet addresses are REFUSED`() {
        assertThat(IpUtils.isAuthorizedLabAddress("8.8.8.8")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("1.1.1.1")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("93.184.216.34")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("172.217.14.206")).isFalse() // google.com-ish public IP
    }

    @Test
    fun `malformed input is refused, not crashed on`() {
        assertThat(IpUtils.isAuthorizedLabAddress("not-an-ip")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("192.168.1")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("192.168.1.1.1")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("")).isFalse()
        assertThat(IpUtils.isAuthorizedLabAddress("999.999.999.999")).isFalse()
    }

    @Test
    fun `subnet base validation mirrors single address validation`() {
        assertThat(IpUtils.isAuthorizedLabSubnetBase("192.168.1")).isTrue()
        assertThat(IpUtils.isAuthorizedLabSubnetBase("8.8.8")).isFalse()
    }

    @Test
    fun `isValidIpv4 accepts well-formed addresses only`() {
        assertThat(IpUtils.isValidIpv4("1.2.3.4")).isTrue()
        assertThat(IpUtils.isValidIpv4("256.1.1.1")).isFalse()
        assertThat(IpUtils.isValidIpv4("1.2.3")).isFalse()
    }
}
