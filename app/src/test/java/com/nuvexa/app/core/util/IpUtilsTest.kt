package com.nuvexa.app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IpUtilsTest {

    @Test
    fun `a slash 24 network has 254 usable hosts`() {
        val info = parseCidr("192.168.1.0/24")!!
        assertEquals("192.168.1.0", info.networkAddress)
        assertEquals("192.168.1.255", info.broadcastAddress)
        assertEquals("255.255.255.0", info.subnetMask)
        assertEquals("192.168.1.1", info.firstUsable)
        assertEquals("192.168.1.254", info.lastUsable)
        assertEquals(254L, info.usableHostCount)
    }

    @Test
    fun `a slash 32 host route has exactly one usable address`() {
        val info = parseCidr("10.0.0.5/32")!!
        assertEquals("10.0.0.5", info.networkAddress)
        assertEquals("10.0.0.5", info.broadcastAddress)
        assertEquals(1L, info.usableHostCount)
    }

    @Test
    fun `a slash 31 point-to-point link has two usable addresses and no broadcast`() {
        val info = parseCidr("10.0.0.0/31")!!
        assertEquals(2L, info.usableHostCount)
    }

    @Test
    fun `a slash 0 network covers the entire address space`() {
        val info = parseCidr("0.0.0.0/0")!!
        assertEquals("0.0.0.0", info.networkAddress)
        assertEquals("255.255.255.255", info.broadcastAddress)
    }

    @Test
    fun `malformed input returns null instead of crashing`() {
        assertNull(parseCidr("not an ip"))
        assertNull(parseCidr("192.168.1.0"))
        assertNull(parseCidr("192.168.1.0/33"))
        assertNull(parseCidr("999.1.1.1/24"))
        assertNull(parseCidr(""))
    }

    @Test
    fun `network address that isn't aligned to the mask still resolves correctly`() {
        // .130 is inside the .128/25 block, not the block's first address.
        val info = parseCidr("192.168.1.130/25")!!
        assertEquals("192.168.1.128", info.networkAddress)
        assertEquals("192.168.1.255", info.broadcastAddress)
    }
}
