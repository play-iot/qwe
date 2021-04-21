package io.zero88.qwe.protocol.network;

import java.util.Map;

import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.exceptions.CommunicationProtocolException;
import io.zero88.qwe.exceptions.NotFoundException;
import io.zero88.qwe.utils.Networks;
import io.github.zero88.utils.Functions;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TransportProtocol implements Ethernet {

    @NonNull
    @JsonUnwrapped
    private IpNetwork ip;
    private int port;
    private boolean canReusePort;

    @JsonCreator
    public static TransportProtocol parse(@NonNull Map<String, Object> data) {
        final String type = Strings.requireNotBlank(data.get("type"), "Missing protocol type");
        if (type.startsWith("udp")) {
            return JsonData.from(data, UdpProtocol.class);
        }
        if (type.startsWith("tcp")) {
            return JsonData.from(data, TcpProtocol.class);
        }
        throw new IllegalArgumentException("Unsupported protocol " + type);
    }

    /**
     * Parse Transport protocol
     *
     * @param identifier IP network identifier
     * @return IP network instance
     * @throws IllegalArgumentException if any invalid data
     * @throws NotFoundException        if interface name is not found
     */
    public static TransportProtocol parse(@NonNull String identifier) {
        final String[] splitter = identifier.split(SPLIT_CHAR, 2);
        if (!splitter[0].matches("(?i)(udp|tcp)([46])?")) {
            throw new IllegalArgumentException("Unsupported protocol " + splitter[0]);
        }
        final String namePart = Strings.requireNotBlank(Functions.getIfThrow(() -> splitter[1]).orElse(""),
                                                        "Missing network interface name");
        final int lastSplitChar = namePart.lastIndexOf(SPLIT_CHAR);
        final String ifName = namePart.substring(0, lastSplitChar);
        final int port = Functions.getIfThrow(() -> Functions.toInt().apply(namePart.substring(lastSplitChar + 1)))
                                  .map(Networks::validPort)
                                  .orElseThrow(() -> new IllegalArgumentException("Missing port"));
        final IpNetwork network = splitter[0].endsWith("6")
                                  ? Ipv6Network.getActiveIpByName(ifName)
                                  : Ipv4Network.getActiveIpByName(ifName);
        if (splitter[0].toLowerCase().startsWith("udp")) {
            return UdpProtocol.builder().ip(network).port(port).canReusePort(true).build();
        }
        return TcpProtocol.builder().ip(network).port(port).canReusePort(true).build();
    }

    @Override
    public abstract @NonNull TransportProtocol isReachable() throws CommunicationProtocolException;

    @Override
    @EqualsAndHashCode.Include
    public @NonNull String identifier() {
        return Ethernet.super.identifier() + SPLIT_CHAR + getPort();
    }

    @Override
    public Integer getIfIndex() {
        return getIp().getIfIndex();
    }

    @Override
    public String getIfName() {
        return getIp().getIfName();
    }

    @Override
    public String getDisplayName() {
        return getIp().getDisplayName();
    }

    @Override
    public String getMacAddress() {
        return getIp().getMacAddress();
    }

    @Override
    public String getCidrAddress() {
        return getIp().getCidrAddress();
    }

    @Override
    public String getHostAddress() {
        return getIp().getHostAddress();
    }

    abstract @NonNull TransportProtocol isPortAvailable() throws CommunicationProtocolException;

}
