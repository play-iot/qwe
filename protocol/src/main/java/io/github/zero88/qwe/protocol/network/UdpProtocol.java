package io.github.zero88.qwe.protocol.network;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;

import io.github.zero88.qwe.exceptions.CommunicationProtocolException;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.NonNull;

@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = UdpProtocol.Builder.class)
public final class UdpProtocol extends TransportProtocol {

    private UdpProtocol(IpNetwork ip, int port, boolean canReusePort) {
        super(ip, port, canReusePort);
    }

    @Override
    public @NonNull String type() {
        return "udp" + getIp().version();
    }

    @Override
    public @NonNull UdpProtocol isReachable() throws CommunicationProtocolException {
        return (UdpProtocol) this.setIp(this.getIp().isReachable()).isPortAvailable();
    }

    @Override
    @NonNull UdpProtocol isPortAvailable() throws CommunicationProtocolException {
        if (isCanReusePort()) {
            return this;
        }
        try (DatagramSocket ignored = new DatagramSocket(getPort(), InetAddress.getByName(getHostAddress()))) {
            return this;
        } catch (Exception ex) {
            throw new CommunicationProtocolException("Port is not available", ex);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder extends TransportBuilder<UdpProtocol, Builder> {

        @Override
        public UdpProtocol build() {
            return new UdpProtocol(Optional.ofNullable(ip()).orElseGet(this::buildIp), port(), canReusePort());
        }

    }

}
