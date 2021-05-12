package io.zero88.qwe.http.server.dynamic.mock;

import io.vertx.core.CompositeFuture;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.micro.MicroContext;
import io.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.zero88.qwe.http.EventMethodDefinition;

public class MockEventOneApiMultiLocService extends MockEventOneApiOneLocService {

    private final String address = MockEventServiceListener.TEST_EVENT_4.getAddress();

    @Override
    public void registerEventBus(EventBusClient eventBus) {
        eventBus.register(address, MockEventServiceListener.TEST_EVENT_LISTENER_4);
    }

    @Override
    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryInvoker discovery = microContext.getLocalInvoker();
        CompositeFuture.all(
            discovery.addEventMessageRecord("ems-4", address, EventMethodDefinition.createDefault("/p", "/:pId")),
            discovery.addEventMessageRecord("ems-4", address,
                                            EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")))
                       .onComplete(r -> r.succeeded());
    }

}
