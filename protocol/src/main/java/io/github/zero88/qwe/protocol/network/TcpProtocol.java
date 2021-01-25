package io.github.zero88.qwe.protocol.network;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Optional;

import io.github.zero88.qwe.exceptions.CommunicationProtocolException;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.NonNull;

@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = TcpProtocol.Builder.class)
public final class TcpProtocol extends TransportProtocol {

    private TcpProtocol(@NonNull IpNetwork ip, int port, boolean canReusePort) {
        super(ip, port, canReusePort);
    }

    @Override
    public @NonNull String type() {
        return "tcp" + getIp().version();
    }

    @Override
    public @NonNull TcpProtocol isReachable() throws CommunicationProtocolException {
        return (TcpProtocol) this.setIp(this.getIp().isReachable()).isPortAvailable();
    }

    @Override
    @NonNull TcpProtocol isPortAvailable() throws CommunicationProtocolException {
        if (isCanReusePort()) {
            return this;
        }
        try (ServerSocket ignored = new ServerSocket(getPort(), 1, InetAddress.getByName(getHostAddress()))) {
            return this;
        } catch (Exception ex) {
            throw new CommunicationProtocolException("Port is not available", ex);
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder extends TransportBuilder<TcpProtocol, TcpProtocol.Builder> {

        @Override
        public TcpProtocol build() {
            return new TcpProtocol(Optional.ofNullable(ip()).orElseGet(this::buildIp), port(), canReusePort());
        }

    }

}
