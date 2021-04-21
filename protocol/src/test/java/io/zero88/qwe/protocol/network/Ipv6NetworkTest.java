package io.zero88.qwe.protocol.network;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.dto.JsonData;
import io.vertx.core.json.JsonObject;

public class Ipv6NetworkTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_serialize_invalid_ipv6() {
        try {
            Ipv6Network.builder().ifIndex(1).ifName("eth0").hostAddress("172.1.0.1").build();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid IPv6 address: 172.1.0.1", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_serialize_invalid_cidr() {
        try {
            Ipv6Network.builder().ifIndex(1).ifName("eth0").cidrAddress("fe80::%wlo1/129").build();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid CIDR address: Invalid prefix length, only [0,128]", e.getMessage());
            throw e;
        }
    }

    @Test
    public void test_serialize() throws JSONException {
        final Ipv6Network wlo1 = Ipv6Network.builder()
                                            .ifIndex(1)
                                            .ifName("wlo1")
                                            .displayName("wlo1")
                                            .macAddress("50-3E-AA-8B-CB-61")
                                            .cidrAddress("fe80:0000:0000:0000:0000:0000:0000:0000%wlo1/64")
                                            .hostAddress("fe80:0000:0000:0000:e30d:4eeb:2108:5ae9%wlo1")
                                            .build();
        System.out.println(wlo1.toJson());
        JsonHelper.assertJson(new JsonObject(
            "{\"ifIndex\":1,\"ifName\":\"wlo1\",\"displayName\":\"wlo1\",\"macAddress\":\"50-3E-AA-8B-CB-61\"," +
            "\"cidrAddress\":\"fe80::%wlo1/64\"," +
            "\"hostAddress\":\"fe80::e30d:4eeb:2108:5ae9%wlo1\",\"type\":\"ipv6\"}"), wlo1.toJson());
    }

    @Test
    public void test_deserialize() {
        Ipv6Network ipv6 = JsonData.from(new JsonObject(
                                             "{\"ifIndex\":1,\"ifName\":\"wlo1\"," + "\"macAddress\":\"50-3E-AA-8B-CB"
                                             + "-61\"," +
                                             "\"cidrAddress\":\"fe80::%wlo1/64\"," + "\"hostAddress\":\"fe80::e30d" + ":4eeb:2108:5ae9%wlo1\"}"),
                                         Ipv6Network.class);
        Assert.assertEquals("ipv6", ipv6.type());
        Assert.assertEquals(1, ipv6.getIfIndex().intValue());
        Assert.assertEquals("wlo1", ipv6.getIfName());
        Assert.assertNull(ipv6.getDisplayName());
        Assert.assertEquals("fe80::e30d:4eeb:2108:5ae9%wlo1", ipv6.getHostAddress());
        Assert.assertEquals("fe80::%wlo1/64", ipv6.getCidrAddress());
        Assert.assertEquals("50-3E-AA-8B-CB-61", ipv6.getMacAddress());
        Assert.assertEquals("ipv6-wlo1", ipv6.identifier());
    }

}
