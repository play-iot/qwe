package io.zero88.qwe.http.server.dynamic.mock;

import io.vertx.core.CompositeFuture;
import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.ContextLookup;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.MicroContext;
import io.zero88.qwe.micro.MicroVerticleProvider;
import io.zero88.qwe.micro.ServiceDiscoveryInvoker;

public class MockEventOneApiOneLocService extends ApplicationVerticle {

    public String configFile() { return "eventService.json"; }

    @Override
    public void onStart() {
        addProvider(new MicroVerticleProvider());
        getEventBus().register(MockEventServiceListener.TEST_EVENT_1.getAddress(),
                               MockEventServiceListener.TEST_EVENT_LISTENER_1)
                     .register(MockEventServiceListener.TEST_EVENT_2.getAddress(),
                               MockEventServiceListener.TEST_EVENT_LISTENER_2)
                     .register(MockEventServiceListener.TEST_EVENT_3.getAddress(),
                               MockEventServiceListener.TEST_EVENT_LISTENER_3);
    }

    @Override
    public void onInstallCompleted(ContextLookup lookup) {
        publishService(lookup.query(MicroContext.class));
    }

    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryInvoker discovery = microContext.getLocalInvoker();
        CompositeFuture.all(discovery.addEventMessageRecord("ems-1", MockEventServiceListener.TEST_EVENT_1.getAddress(),
                                                            EventMethodDefinition.createDefault("/hey", "/:id")),
                            discovery.addEventMessageRecord("ems-2", MockEventServiceListener.TEST_EVENT_2.getAddress(),
                                                            EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")),
                            discovery.addEventMessageRecord("ems-3", MockEventServiceListener.TEST_EVENT_3.getAddress(),
                                                            EventMethodDefinition.createDefault("/x/:xId/y", "/:yId",
                                                                                                false)))
                       .onComplete(ar -> ar.succeeded());
    }

}
