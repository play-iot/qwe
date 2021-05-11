package io.zero88.qwe.protocol.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.zero88.qwe.protocol.CommunicationProtocolException;
import io.zero88.qwe.exceptions.DataNotFoundException;
import io.zero88.qwe.utils.Networks;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = Ipv4Network.Builder.class)
public final class Ipv4Network extends IpNetwork<Ipv4Network> implements Ethernet {

    private static final String IPV4_REGEX
        = "(([0-1]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([0-1]?\\d{1,2}|2[0-4]\\d|25[0-5])";
    @Setter
    @Accessors(chain = true)
    private String broadcastAddress;

    private Ipv4Network(Integer ifIndex, String ifName, String displayName, String macAddress, String cidrAddress,
                        String hostAddress, String broadcastAddress) {
        super(ifIndex, ifName, displayName, macAddress, cidrAddress, hostAddress);
        this.broadcastAddress = broadcastAddress;
        this.validate();
    }

    public static Ipv4Network from(@NonNull NetworkInterface ni, @NonNull InterfaceAddress ia) {
        return new Ipv4Network(ni.getIndex(), ni.getName(), ni.getDisplayName(), mac(ni), cidr(ia),
                               ia.getAddress().getHostAddress(), ia.getBroadcast().getHostAddress());
    }

    public static List<Ipv4Network> getActiveIps() {
        return getActiveInterfaces(networkInterface -> true, Networks.IS_V4, Ipv4Network::from);
    }

    /**
     * Find active IPv4 by interface name
     *
     * @param interfaceName interface name
     * @return IPv4 network
     * @throws DataNotFoundException if interface name is not found
     */
    public static Ipv4Network getActiveIpByName(String interfaceName) {
        return getActiveIpByName(interfaceName, Networks.IS_V4, Ipv4Network::from);
    }

    public static Ipv4Network getFirstActiveIp() {
        return getFirstActiveIp(Networks.IS_V4, Ipv4Network::from);
    }

    public static Ipv4Network getActiveIpByBroadcast(@NonNull String broadcast) {
        return getActiveIpWithoutInterface(
            Networks.IS_V4.and(ia -> ia.getBroadcast().getHostAddress().equals(broadcast)), Ipv4Network::from);
    }

    static String cidr(@NonNull InterfaceAddress interfaceAddress) {
        final InetAddress address = interfaceAddress.getAddress();
        final short prefixLength = interfaceAddress.getNetworkPrefixLength();
        if (!(address instanceof Inet4Address)) {
            throw new IllegalArgumentException("Given interface address is not IPv4");
        }
        return Arrays.stream(intToByteArray(byteArrayToInt(address.getAddress()) & mask(prefixLength)))
                     .mapToObj(String::valueOf)
                     .collect(Collectors.joining(".")) + "/" + prefixLength;
    }

    private static int byteArrayToInt(byte[] ba) {
        return ba[3] & 0xFF | (ba[2] & 0xFF) << 8 | (ba[1] & 0xFF) << 16 | (ba[0] & 0xFF) << 24;
    }

    private static int[] intToByteArray(int a) {
        return new int[] {(a >> 24) & 0xFF, (a >> 16) & 0xFF, (a >> 8) & 0xFF, a & 0xFF};
    }

    private static int mask(final int length) {
        int l = 0;
        int shift = 31;
        for (int i = 0; i < length; i++) {
            l |= 1 << shift--;
        }
        return l;
    }

    @Override
    public int version() {
        return 4;
    }

    String validateIpAddress(String address) {
        if (Strings.isNotBlank(address) && !address.matches(IPV4_REGEX)) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + address);
        }
        return address;
    }

    @Override
    int maxPrefixLength() {
        return 32;
    }

    @Override
    Ipv4Network validate() {
        setBroadcastAddress(super.validate().validateIpAddress(getBroadcastAddress()));
        return this;
    }

    @Override
    protected Ipv4Network reload(@NonNull Ipv4Network network) {
        return super.reload(network).setBroadcastAddress(network.getBroadcastAddress());
    }

    @Override
    public Ipv4Network isReachable() throws CommunicationProtocolException {
        return isReachable(Networks.IS_V4, Ipv4Network::from, Ipv4Network::getFirstActiveIp);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends EthernetBuilder<Ipv4Network, Builder> {

        @Override
        public Ipv4Network build() {
            return new Ipv4Network(ifIndex(), ifName(), displayName(), macAddress(), cidrAddress(), hostAddress(),
                                   broadcastAddress);
        }

    }

}
