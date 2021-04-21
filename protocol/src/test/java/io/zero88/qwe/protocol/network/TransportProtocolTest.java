package io.zero88.qwe.protocol.network;

import org.junit.Assert;
import org.junit.Test;

public class TransportProtocolTest {

    @Test
    public void test_parse_udp4() {
        final Ipv4Network activeIp = Ipv4Network.getFirstActiveIp();
        final TransportProtocol protocol = TransportProtocol.parse("udp4-" + activeIp.getIfName() + "-47808");
        Assert.assertEquals("udp4", protocol.type());
        Assert.assertEquals(activeIp, protocol.getIp());
        Assert.assertEquals(47808, protocol.getPort());
    }

}
