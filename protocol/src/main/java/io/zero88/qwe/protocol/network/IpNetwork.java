package io.zero88.qwe.protocol.network;

import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.exceptions.CommunicationProtocolException;
import io.zero88.qwe.exceptions.NotFoundException;
import io.zero88.qwe.protocol.CommunicationProtocol;
import io.zero88.qwe.utils.Networks;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import inet.ipaddr.IPAddressString;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class IpNetwork<T extends IpNetwork> implements Ethernet {

    private Integer ifIndex;
    private String ifName;
    private String displayName;
    private String macAddress;
    private String cidrAddress;
    private String hostAddress;

    @JsonCreator
    public static IpNetwork parse(@NonNull Map<String, Object> data) {
        final String type = Strings.requireNotBlank(data.get("type"), "Missing protocol type");
        if (type.equalsIgnoreCase("ipv6")) {
            return JsonData.from(data, Ipv6Network.class);
        }
        return JsonData.from(data, Ipv4Network.class);
    }

    /**
     * Parse IP network
     *
     * @param identifier IP network identifier
     * @return IP network instance
     * @throws NotFoundException if interface name is not found
     */
    public static IpNetwork parse(@NonNull String identifier) {
        String[] splitter = identifier.split(CommunicationProtocol.SPLIT_CHAR, 2);
        if (splitter[0].equalsIgnoreCase("ipv4")) {
            return Ipv4Network.getActiveIpByName(getInterfaceName(splitter[1]));
        }
        if (splitter[0].equalsIgnoreCase("ipv6")) {
            return Ipv6Network.getActiveIpByName(getInterfaceName(splitter[1]));
        }
        return Ipv4Network.getActiveIpByName(getInterfaceName(splitter[0]));
    }

    static <T extends IpNetwork> List<T> getActiveInterfaces(@NonNull Predicate<NetworkInterface> interfacePredicate, @NonNull Predicate<InterfaceAddress> addressPredicate,
                                                             @NonNull BiFunction<NetworkInterface, InterfaceAddress,
                                                                                    T> parser) {
        List<T> list = new ArrayList<>();
        Enumeration<NetworkInterface> nets = Networks.getNetworkInterfaces();
        while (nets.hasMoreElements()) {
            final NetworkInterface networkInterface = nets.nextElement();
            try {
                if (!networkInterface.isUp() || !interfacePredicate.test(networkInterface)) {
                    continue;
                }
            } catch (SocketException ignored) {
            }
            networkInterface.getInterfaceAddresses()
                            .stream()
                            .filter(addressPredicate)
                            .findFirst()
                            .map(interfaceAddress -> parser.apply(networkInterface, interfaceAddress))
                            .ifPresent(list::add);
        }
        return list;
    }

    static <T extends IpNetwork> T getActiveIpByName(String interfaceName, @NonNull Predicate<InterfaceAddress> iaPredicate,
                                                     @NonNull BiFunction<NetworkInterface, InterfaceAddress, T> parser) {
        return getActiveInterfaces(ni -> ni.getName().equalsIgnoreCase(getInterfaceName(interfaceName)), iaPredicate,
                                   parser).stream().findFirst().orElseThrow(notFound(interfaceName));
    }

    static <T extends IpNetwork> T getFirstActiveIp(@NonNull Predicate<InterfaceAddress> iaPredicate,
                                                    @NonNull BiFunction<NetworkInterface, InterfaceAddress, T> parser) {
        return getActiveInterfaces(ni -> true, iaPredicate, parser).stream().findFirst().orElseThrow(notFound(null));
    }

    static <T extends IpNetwork> T getActiveIpWithoutInterface(@NonNull Predicate<InterfaceAddress> iaPredicate,
                                                               @NonNull BiFunction<NetworkInterface, InterfaceAddress
                                                                                      , T> parser) {
        return getActiveInterfaces(ni -> true, iaPredicate, parser).stream().findFirst().orElseThrow(notFound(null));
    }

    private static String cidr(@NonNull InterfaceAddress interfaceAddress) {
        if (interfaceAddress.getAddress() instanceof Inet6Address) {
            return Ipv6Network.cidr(interfaceAddress);
        }
        return Ipv4Network.cidr(interfaceAddress);
    }

    private static Supplier<NotFoundException> notFound(String interfaceName) {
        return () -> new NotFoundException("Not found active IP network interface" +
                                           Optional.ofNullable(interfaceName).map(n -> " with name " + n).orElse(""));
    }

    private static String getInterfaceName(String interfaceName) {
        return Strings.requireNotBlank(interfaceName, "Missing interface name");
    }

    static String mac(@NonNull NetworkInterface networkInterface) {
        try {
            return mac(networkInterface.getHardwareAddress());
        } catch (SocketException | NullPointerException e) {
            if (log.isDebugEnabled()) {
                log.warn("Cannot compute MAC address");
            }
            return null;
        }
    }

    public static String mac(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }

    public static boolean isIpv6(String address) {
        return Strings.isNotBlank(address) && new IPAddressString(address).isIPv6();
    }

    public static boolean isIpv4(String address) {
        return Strings.isNotBlank(address) && new IPAddressString(address).isIPv4();
    }

    public abstract int version();

    abstract String validateIpAddress(String address);

    abstract int maxPrefixLength();

    @SuppressWarnings("unchecked")
    T validate() {
        hostAddress = validateIpAddress(hostAddress);
        cidrAddress = validateCidrAddress(cidrAddress);
        return (T) this;
    }

    private int validatePrefixLength(int prefixLength) {
        if (prefixLength < 0 || prefixLength > maxPrefixLength()) {
            throw new IllegalArgumentException("Invalid prefix length, only [0," + maxPrefixLength() + "]");
        }
        return prefixLength;
    }

    private String validateCidrAddress(String cidr) {
        if (Strings.isBlank(cidr)) {
            return cidr;
        }
        String[] splits = cidr.split("/", 2);
        return Functions.getOrThrow(() -> validateIpAddress(splits[0]) + "/" + validatePrefixLength(
            Functions.toInt().apply(Functions.getOrDefault(() -> splits[1], () -> String.valueOf(maxPrefixLength())))),
                                    t -> new IllegalArgumentException("Invalid CIDR address: " + t.getMessage()));
    }

    protected T reload(@NonNull T network) {
        return this.setIfIndex(network.getIfIndex())
                   .setIfName(network.getIfName())
                   .setDisplayName(network.getDisplayName())
                   .setMacAddress(network.getMacAddress())
                   .setCidrAddress(network.getCidrAddress())
                   .setHostAddress(network.getHostAddress())
                   .validate();
    }

    @Override
    public final @NonNull String type() {
        return "ipv" + version();
    }

    @Override
    public abstract T isReachable() throws CommunicationProtocolException;

    @Override
    @EqualsAndHashCode.Include
    public @NonNull String identifier() {
        return Ethernet.super.identifier();
    }

    @JsonIgnore
    public String getSubnetAddress() {
        return cidrAddress.substring(0, cidrAddress.lastIndexOf("/"));
    }

    @JsonIgnore
    public short getSubnetPrefixLength() {
        return Short.parseShort(cidrAddress.substring(cidrAddress.lastIndexOf("/") + 1));
    }

    T isReachable(@NonNull Predicate<InterfaceAddress> predicate, @NonNull BiFunction<NetworkInterface, InterfaceAddress, T> parser, @NonNull Supplier<T> fallback)
        throws CommunicationProtocolException {
        if (Strings.isBlank(getIfName()) && Strings.isBlank(getCidrAddress())) {
            return reload(fallback.get());
        }
        final Predicate<InterfaceAddress> iaPredicate = ia -> Optional.ofNullable(getCidrAddress())
                                                                      .map(cidr -> cidr.equals(cidr(ia)))
                                                                      .orElse(true);
        final List<T> interfaces = getActiveInterfaces(this::checkInterface, predicate.and(iaPredicate), parser);
        final String name = Strings.isBlank(getIfName()) ? getCidrAddress() : getIfName();
        if (interfaces.isEmpty()) {
            throw new CommunicationProtocolException("Interface name " + name + " is obsolete or down");
        }
        if (interfaces.size() > 1) {
            log.warn("Has more than one CIDR with same given interface {}", name);
        }
        return reload(interfaces.get(0));
    }

    private boolean checkInterface(NetworkInterface ni) {
        return Optional.ofNullable(getIfName()).map(ifName -> ifName.equalsIgnoreCase(ni.getName())).orElse(true) &&
               Optional.ofNullable(getMacAddress())
                       .flatMap(mac -> Optional.ofNullable(mac(ni)).map(actualMac -> actualMac.equalsIgnoreCase(mac)))
                       .orElse(true);
    }

}
