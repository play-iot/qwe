package io.github.zero88.msa.blueprint.utils;

import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NetworksTest {

    @Before
    public void setup() {
        Networks.cleanup();
        System.setProperty(Networks.CLUSTER_PUBLIC_PROP, String.valueOf(true));
        System.setProperty(Networks.CLUSTER_PUBLIC_HOST_PROP, "");
        System.setProperty(Networks.CLUSTER_PUBLIC_PORT_PROP, "");
        System.setProperty(Networks.CLUSTER_PUBLIC_EVENTBUS_PORT_PROP, "");
    }

    @Test
    public void test_computeAddress_notEnabled() {
        System.setProperty(Networks.CLUSTER_PUBLIC_PROP, String.valueOf(false));
        Assert.assertNull(Networks.computeClusterPublicUrl("127.0.0.1:9090"));
    }

    @Test
    public void test_computeAddress_noEnv() {
        final InetSocketAddress address = Networks.computeClusterPublicUrl("127.0.0.1:9090");
        Assert.assertNotNull(address);
        Assert.assertEquals("127.0.0.1", address.getHostName());
        Assert.assertEquals("127.0.0.1", address.getHostString());
        Assert.assertEquals("127.0.0.1:9090", address.toString());
        Assert.assertEquals(9090, address.getPort());
    }

    @Test
    public void test_computeAddress_hasEnv() {
        System.setProperty(Networks.CLUSTER_PUBLIC_HOST_PROP, "10.10.10.10");
        System.setProperty(Networks.CLUSTER_PUBLIC_PORT_PROP, "1234");
        final InetSocketAddress address = Networks.computeClusterPublicUrl("127.0.0.1:9090");
        Assert.assertNotNull(address);
        Assert.assertEquals("10.10.10.10", address.getHostName());
        Assert.assertEquals("10.10.10.10", address.getHostString());
        Assert.assertEquals("10.10.10.10:1234", address.toString());
        Assert.assertEquals(1234, address.getPort());
    }

}
