package io.github.zero88.qwe.http.server.dynamic.mock;

import io.github.zero88.qwe.component.ApplicationVerticle;
import io.github.zero88.qwe.component.ContextLookup;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.micro.MicroContext;
import io.github.zero88.qwe.micro.MicroserviceProvider;
import io.github.zero88.qwe.micro.ServiceDiscoveryController;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;
import io.reactivex.Single;

public class MockEventOneApiOneLocService extends ApplicationVerticle {

    public String configFile() { return "eventService.json"; }

    @Override
    public void start() {
        super.start();
        addProvider(new MicroserviceProvider());
    }

    @Override
    public void onInstallCompleted(ContextLookup lookup) {
        publishService(lookup.query(MicroContext.class));
    }

    @Override
    public void registerEventbus(EventbusClient eventbus) {
        eventbus.register(MockEventServiceListener.TEST_EVENT_1, MockEventServiceListener.TEST_EVENT_LISTENER_1);
        eventbus.register(MockEventServiceListener.TEST_EVENT_2, MockEventServiceListener.TEST_EVENT_LISTENER_2);
        eventbus.register(MockEventServiceListener.TEST_EVENT_3, MockEventServiceListener.TEST_EVENT_LISTENER_3);
    }

    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryController controller = microContext.getLocalController();
        Single.concat(controller.addEventMessageRecord("ems-1", MockEventServiceListener.TEST_EVENT_1.getAddress(),
                                                       EventMethodDefinition.createDefault("/hey", "/:id")),
                      controller.addEventMessageRecord("ems-2", MockEventServiceListener.TEST_EVENT_2.getAddress(),
                                                       EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")),
                      controller.addEventMessageRecord("ems-3", MockEventServiceListener.TEST_EVENT_3.getAddress(),
                                                       EventMethodDefinition.createDefault("/x/:xId/y", "/:yId",
                                                                                           false))).subscribe();
    }

}
