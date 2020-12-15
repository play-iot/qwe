package io.github.zero88.msa.bp.micro.discovery.mock;


import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.micro.discovery.GatewayServiceInvoker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MockServiceInvoker implements GatewayServiceInvoker {

    private final String gatewayAddress;
    private final EventbusClient client;
    private final String destination;

    @Override
    public @NonNull String gatewayAddress() {
        return gatewayAddress;
    }

    @Override
    public @NonNull String destination() {
        return destination;
    }

    @Override
    public String requester() {
        return "discovery.test";
    }

    @Override
    public String serviceLabel() {
        return "Mock Service";
    }

    @Override
    public EventbusClient transporter() {
        return client;
    }

}
