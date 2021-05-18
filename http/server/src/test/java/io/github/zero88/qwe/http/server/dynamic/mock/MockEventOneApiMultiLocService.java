package io.github.zero88.qwe.http.server.dynamic.mock;

import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.micro.MicroContext;
import io.github.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.github.zero88.qwe.micro.http.EventMethodDefinition;
import io.reactivex.Single;

public class MockEventOneApiMultiLocService extends MockEventOneApiOneLocService {

    @Override
    public void registerEventbus(EventbusClient eventbus) {
        eventbus.register(MockEventServiceListener.TEST_EVENT_4, MockEventServiceListener.TEST_EVENT_LISTENER_4);
    }

    @Override
    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryInvoker controller = microContext.getLocalInvoker();
        Single.concat(controller.addEventMessageRecord("ems-4", MockEventServiceListener.TEST_EVENT_4.getAddress(),
                                                       EventMethodDefinition.createDefault("/p", "/:pId")),
                      controller.addEventMessageRecord("ems-4", MockEventServiceListener.TEST_EVENT_4.getAddress(),
                                                       EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")))
              .subscribe();
    }

}