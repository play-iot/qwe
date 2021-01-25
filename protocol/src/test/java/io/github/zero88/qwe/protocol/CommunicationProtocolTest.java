package io.github.zero88.qwe.protocol;

import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.qwe.dto.JsonData;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.NotFoundException;

import io.github.zero88.qwe.protocol.network.Ipv4Network;
import io.github.zero88.qwe.protocol.network.TcpProtocol;
import io.github.zero88.qwe.protocol.network.UdpProtocol;

public class CommunicationProtocolTest {

    @Test(expected = NotFoundException.class)
    public void test_parse_not_found_ipv4() {
        CommunicationProtocol.parse("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_parse_invalid_ipv4() {
        CommunicationProtocol.parse("");
    }

    @Test
    public void test_parse_available_ipv4() {
        final Ipv4Network activeIp = Ipv4Network.getFirstActiveIp();
        final CommunicationProtocol protocol = CommunicationProtocol.parse(activeIp.identifier());
        Assert.assertEquals(activeIp, protocol);
    }

    @Test
    public void test_deserialize_udp4() {
        final CommunicationProtocol protocol = JsonData.from(
            "{\"type\":\"udp4\",\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
            "\"hostAddress\":\"172.1.0.2\",\"broadcastAddress\":\"172.1.0.255\",\"port\":8080}",
            CommunicationProtocol.class);
        Assert.assertEquals("udp4", protocol.type());
        Assert.assertEquals("udp4-eth0-8080", protocol.identifier());
        Assert.assertTrue(protocol instanceof UdpProtocol);
        UdpProtocol udp = (UdpProtocol) protocol;
        Assert.assertEquals(1, udp.getIfIndex().intValue());
        Assert.assertEquals("eth0", udp.getIfName());
        Assert.assertNull(udp.getDisplayName());
        Assert.assertEquals(8080, udp.getPort());
        Assert.assertEquals("172.1.0.2", udp.getHostAddress());
        Assert.assertEquals("172.1.0.1/16", udp.getCidrAddress());
        Assert.assertTrue(udp.getIp() instanceof Ipv4Network);
        Assert.assertEquals("172.1.0.255", ((Ipv4Network) udp.getIp()).getBroadcastAddress());
    }

    @Test
    public void test_deserialize_tcp4() {
        final CommunicationProtocol protocol = JsonData.from(
            "{\"type\":\"tcp4\",\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
            "\"hostAddress\":\"172.1.0.2\",\"broadcastAddress\":\"172.1.0.255\",\"port\":8080}",
            CommunicationProtocol.class);
        Assert.assertEquals("tcp4", protocol.type());
        Assert.assertEquals("tcp4-eth0-8080", protocol.identifier());
        Assert.assertTrue(protocol instanceof TcpProtocol);
        TcpProtocol udp = (TcpProtocol) protocol;
        Assert.assertEquals(1, udp.getIfIndex().intValue());
        Assert.assertEquals("eth0", udp.getIfName());
        Assert.assertNull(udp.getDisplayName());
        Assert.assertEquals(8080, udp.getPort());
        Assert.assertEquals("172.1.0.2", udp.getHostAddress());
        Assert.assertEquals("172.1.0.1/16", udp.getCidrAddress());
        Assert.assertTrue(udp.getIp() instanceof Ipv4Network);
        Assert.assertEquals("172.1.0.255", ((Ipv4Network) udp.getIp()).getBroadcastAddress());
    }

    @Test
    public void test_deserialize_ipv4() {
        final CommunicationProtocol protocol = JsonData.from(
            "{\"type\":\"ipv4\",\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
            "\"hostAddress\":\"172.1.0.2\",\"broadcastAddress\":\"172.1.0.255\"}", CommunicationProtocol.class);
        Assert.assertEquals("ipv4", protocol.type());
        Assert.assertEquals("ipv4-eth0", protocol.identifier());
        Assert.assertTrue(protocol instanceof Ipv4Network);
        Ipv4Network ipv4 = (Ipv4Network) protocol;
        Assert.assertEquals(1, ipv4.getIfIndex().intValue());
        Assert.assertEquals("eth0", ipv4.getIfName());
        Assert.assertNull(ipv4.getDisplayName());
        Assert.assertEquals("172.1.0.2", ipv4.getHostAddress());
        Assert.assertEquals("172.1.0.255", ipv4.getBroadcastAddress());
        Assert.assertEquals("172.1.0.1/16", ipv4.getCidrAddress());
    }

    @Test(expected = CarlException.class)
    public void test_deserialize_invalid_ipv6() {
        JsonData.from("{\"type\":\"ipv6\",\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
                      "\"hostAddress\":\"172.1.0.2\"}", CommunicationProtocol.class);
    }

}
