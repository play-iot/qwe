package cloud.playio.qwe.utils;

import java.net.InetSocketAddress;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetworkUtilsTest {

    @Test
    public void test_get_public_ipv4() {
        final String address = NetworkUtils.getPublicIpv4();
        System.out.println(address);
        Assertions.assertNotNull(address);
    }

    @Test
    public void test_get_socket_ipv4_address() {
        final InetSocketAddress address = NetworkUtils.toSocketIpv4Address("127.0.0.1:9090");
        Assertions.assertNotNull(address);
        Assertions.assertEquals("127.0.0.1", address.getHostName());
        Assertions.assertEquals("127.0.0.1", address.getHostString());
        Assertions.assertEquals(9090, address.getPort());
        Assertions.assertEquals("127.0.0.1/<unresolved>:9090", address.toString());
    }

}
