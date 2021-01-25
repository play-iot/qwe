package io.github.zero88.qwe.protocol.network;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.qwe.TestHelper.JsonHelper;
import io.github.zero88.qwe.dto.JsonData;
import io.vertx.core.json.JsonObject;

public class UdpProtocolTest {

    @Test
    public void test_serialize() throws JSONException {
        final UdpProtocol protocol = UdpProtocol.builder()
                                                .ifIndex(1)
                                                .ifName("eth0")
                                                .cidrAddress("172.1.0.1/16")
                                                .hostAddress("172.1.0.2")
                                                .broadcastAddress("172.1.0.255")
                                                .port(8080)
                                                .build();
        System.out.println(protocol.toJson());
        JsonHelper.assertJson(new JsonObject("{\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
                                             "\"hostAddress\":\"172.1.0.2\",\"broadcastAddress\":\"172.1.0.255\"," +
                                             "\"type\":\"udp4\",\"port\":8080,\"canReusePort\":false}"),
                              protocol.toJson());
    }

    @Test
    public void test_deserialize_udp4() {
        final UdpProtocol udp = JsonData.from(
            "{\"type\":\"udp4\",\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
            "\"hostAddress\":\"172.1.0.2\",\"broadcastAddress\":\"172.1.0.255\",\"port\":8080}", UdpProtocol.class);
        Assert.assertEquals("udp4", udp.type());
        Assert.assertEquals(1, udp.getIfIndex().intValue());
        Assert.assertEquals("eth0", udp.getIfName());
        Assert.assertNull(udp.getDisplayName());
        Assert.assertEquals(8080, udp.getPort());
        Assert.assertEquals("172.1.0.2", udp.getHostAddress());
        Assert.assertEquals("172.1.0.1/16", udp.getCidrAddress());
        Assert.assertTrue(udp.getIp() instanceof Ipv4Network);
        Assert.assertEquals("172.1.0.255", ((Ipv4Network) udp.getIp()).getBroadcastAddress());
        Assert.assertEquals("udp4-eth0-8080", udp.identifier());
    }

    @Test
    public void test_equals_udp4() {
        final UdpProtocol udp1 = JsonData.from(
            "{\"type\":\"udp4\",\"ifIndex\":1,\"ifName\":\"eth0\",\"cidrAddress\":\"172.1.0.1/16\"," +
            "\"hostAddress\":\"172.1.0.2\",\"broadcastAddress\":\"172.1.0.255\",\"port\":8080}", UdpProtocol.class);
        final UdpProtocol udp2 = UdpProtocol.builder()
                                            .ifIndex(2)
                                            .ifName("eth0")
                                            .cidrAddress("172.1.0.1/16")
                                            .hostAddress("172.1.0.2")
                                            .broadcastAddress("172.1.0.255")
                                            .port(8080)
                                            .build();
        System.out.println(udp1.identifier());
        Assert.assertEquals(udp1, udp2);
        Assert.assertEquals(udp1.identifier(), udp2.identifier());
    }

}
