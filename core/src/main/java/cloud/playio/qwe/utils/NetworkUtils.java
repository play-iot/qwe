package cloud.playio.qwe.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.zero88.utils.Strings;

import cloud.playio.qwe.exceptions.NetworkException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NetworkUtils {

    public static final Predicate<InetAddress> IS_NAT_V4 = address -> !address.isAnyLocalAddress() &&
                                                                      !address.isMulticastAddress() &&
                                                                      !address.isLoopbackAddress() &&
                                                                      address instanceof Inet4Address;
    public static final Predicate<InterfaceAddress> IS_V4 = address -> IS_NAT_V4.test(address.getAddress());

    private static final Logger logger = LogManager.getLogger(NetworkUtils.class);
    private static final String GLOBAL_ADDRESS = "0.0.0.0";
    private static final List<String> BLACKLIST_ADDRESSES = Arrays.asList(GLOBAL_ADDRESS, "127.0.0.1", "localhost");

    public static int validPort(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port is not in range [1, 65535]");
        }
        return port;
    }

    public static int validPort(int port, int fallback) {
        return port < 1 || port > 65535 ? validPort(fallback) : port;
    }

    public static String getPublicIpv4() {
        return getAddress(null, IS_NAT_V4);
    }

    public static String getPublicIpv4(String givenHost) {
        return getAddress(givenHost, IS_NAT_V4);
    }

    public static InetSocketAddress toSocketIpv4Address(String socketAddress) {
        if (Strings.isBlank(socketAddress)) {
            return null;
        }
        final int index = socketAddress.lastIndexOf(":");
        if (index == -1) {
            return null;
        }
        return toSocketIpv4Address(socketAddress.substring(0, index), socketAddress.substring(index + 1));
    }

    public static InetSocketAddress toSocketIpv4Address(String givenHost, String givenPort) {
        return toSocketIpv4Address(givenHost, Strings.convertToInt(givenPort, -1));
    }

    public static InetSocketAddress toSocketIpv4Address(String givenHost, int givenPort) {
        return InetSocketAddress.createUnresolved(givenHost, validPort(givenPort));
    }

    private static String getAddress(String givenHost, Predicate<InetAddress> predicate) {
        if (!Strings.isBlank(givenHost) && !BLACKLIST_ADDRESSES.contains(givenHost)) {
            return givenHost;
        }
        Enumeration<NetworkInterface> nets = getNetworkInterfaces();
        List<InetAddress> usableINetAddresses = new ArrayList<>();
        while (nets.hasMoreElements()) {
            NetworkInterface networkInterface = nets.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (logger.isTraceEnabled()) {
                    logger.trace("Found INetAddress[{}] on interface[{}]", address.toString(),
                                 networkInterface.getName());
                }
                if (predicate.test(address)) {
                    usableINetAddresses.add(address);
                }
            }
        }

        if (usableINetAddresses.size() >= 1) {
            // TODO: switch case between Docker environment and Bare-metal server | device
            if (usableINetAddresses.size() != 1) {
                logger.warn("There are more than one NAT addresses: {}", usableINetAddresses);
            }
            final InetAddress inetAddress = usableINetAddresses.get(0);
            logger.info("Pick IPv4 address[{}]", inetAddress.getHostAddress());
            return inetAddress.getHostAddress();
        }
        logger.warn("Not found usable INet address, fallback to loopback");
        InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
        return Objects.isNull(loopbackAddress) ? GLOBAL_ADDRESS : loopbackAddress.getHostAddress();
    }

    private static Enumeration<NetworkInterface> getNetworkInterfaces() {
        try {
            return NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new NetworkException("Cannot get the network interfaces", e);
        }
    }

}
