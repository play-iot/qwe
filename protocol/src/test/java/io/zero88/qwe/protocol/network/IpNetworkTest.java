package io.zero88.qwe.protocol.network;

import org.junit.Assert;
import org.junit.Test;

public class IpNetworkTest {

    @Test
    public void test_valid_ipv4() {
        Assert.assertTrue(IpNetwork.isIpv4("255.255.255.255"));
        Assert.assertTrue(IpNetwork.isIpv4("0.0.0.0"));
        Assert.assertTrue(IpNetwork.isIpv4("192.168.1.1"));
        Assert.assertTrue(IpNetwork.isIpv4("10.0.1.1"));
    }

    @Test
    public void test_invalid_ipv4() {
        Assert.assertFalse(IpNetwork.isIpv4("256.255.255.255"));
        Assert.assertFalse(IpNetwork.isIpv4("0.0.0.a"));
        Assert.assertFalse(IpNetwork.isIpv4(""));
    }

}
