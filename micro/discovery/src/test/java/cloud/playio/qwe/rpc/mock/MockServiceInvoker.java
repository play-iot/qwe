package cloud.playio.qwe.rpc.mock;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.rpc.GatewayServiceInvoker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MockServiceInvoker implements GatewayServiceInvoker {

    private final String gatewayAddress;
    private final EventBusClient client;
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
    public EventBusClient transporter() {
        return client;
    }

    @Override
    public @NonNull SharedDataLocalProxy sharedData() {
        return client.sharedData();
    }

}
