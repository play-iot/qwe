package io.zero88.qwe.protocol.network;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import io.zero88.qwe.protocol.CommunicationProtocolException;
import io.zero88.qwe.exceptions.DataNotFoundException;
import io.github.zero88.utils.Functions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import inet.ipaddr.IPAddressString;
import inet.ipaddr.IPAddressStringParameters;
import inet.ipaddr.ipv6.IPv6Address;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = Ipv6Network.Builder.class)
public final class Ipv6Network extends IpNetwork<Ipv6Network> implements Ethernet {

    private static final Predicate<InetAddress> IS_NAT_V6 = address -> !address.isAnyLocalAddress() &&
                                                                       !address.isMulticastAddress() &&
                                                                       !address.isLoopbackAddress() &&
                                                                       address instanceof Inet6Address;
    private static final Predicate<InterfaceAddress> IS_V6 = address -> IS_NAT_V6.test(address.getAddress());

    private Ipv6Network(Integer ifIndex, String ifName, String displayName, String macAddress, String cidrAddress,
                        String hostAddress) {
        super(ifIndex, ifName, displayName, macAddress, cidrAddress, hostAddress);
        validate();
    }

    public static Ipv6Network from(NetworkInterface ni, InterfaceAddress ia) {
        return new Ipv6Network(ni.getIndex(), ni.getName(), ni.getDisplayName(), mac(ni), cidr(ia),
                               ia.getAddress().getHostAddress());
    }

    static String cidr(@NonNull InterfaceAddress interfaceAddress) {
        final InetAddress address = interfaceAddress.getAddress();
        final short prefixLength = interfaceAddress.getNetworkPrefixLength();
        if (!(address instanceof Inet6Address)) {
            throw new IllegalArgumentException("Given interface address is not IPv6");
        }
        return new IPv6Address((Inet6Address) address, (int) prefixLength).toPrefixBlock().toCanonicalString();
    }

    public static List<Ipv6Network> getActiveIps() {
        return getActiveInterfaces(networkInterface -> true, IS_V6, Ipv6Network::from);
    }

    /**
     * Find active IPv6 by interface name
     *
     * @param interfaceName interface name
     * @return IPv6 network
     * @throws DataNotFoundException if interface name is not found
     */
    public static Ipv6Network getActiveIpByName(String interfaceName) {
        return getActiveIpByName(interfaceName, IS_V6, Ipv6Network::from);
    }

    public static Ipv6Network getFirstActiveIp() {
        return getFirstActiveIp(IS_V6, Ipv6Network::from);
    }

    @Override
    public int version() {
        return 6;
    }

    @Override
    String validateIpAddress(String address) {
        if (Objects.isNull(address)) {
            return null;
        }
        final IPAddressStringParameters params = new IPAddressStringParameters.Builder().allowIPv6(true)
                                                                                        .allowIPv4(false)
                                                                                        .allowMask(false)
                                                                                        .allowPrefix(false)
                                                                                        .allowPrefixOnly(false)
                                                                                        .allowWildcardedSeparator(false)
                                                                                        .toParams();
        return Functions.getOrThrow(t -> new IllegalArgumentException("Invalid IPv6 address: " + address, t),
                                    () -> new IPAddressString(address, params).toAddress()).toCanonicalString();
    }

    @Override
    int maxPrefixLength() {
        return 128;
    }

    @Override
    public Ipv6Network isReachable() throws CommunicationProtocolException {
        return isReachable(IS_V6, Ipv6Network::from, Ipv6Network::getFirstActiveIp);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends EthernetBuilder<Ipv6Network, Builder> {

        @Override
        public Ipv6Network build() {
            return new Ipv6Network(ifIndex(), ifName(), displayName(), macAddress(), cidrAddress(), hostAddress());
        }

    }

}
