package io.zero88.qwe.http.server.dynamic.mock;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.ContextLookup;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.MicroContext;
import io.zero88.qwe.micro.MicroVerticleProvider;
import io.zero88.qwe.micro.RecordHelper;
import io.zero88.qwe.micro.ServiceDiscoveryApi;

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
        publishService(Objects.requireNonNull(lookup.query(MicroContext.class)));
    }

    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryApi discovery = microContext.getDiscovery();
        CompositeFuture.all(Stream.of(RecordHelper.create("ems-1", MockEventServiceListener.TEST_EVENT_1.getAddress(),
                                                          EventMethodDefinition.createDefault("/hey", "/:id")),
                                      RecordHelper.create("ems-2", MockEventServiceListener.TEST_EVENT_2.getAddress(),
                                                          EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")),
                                      RecordHelper.create("ems-3", MockEventServiceListener.TEST_EVENT_3.getAddress(),
                                                          EventMethodDefinition.createDefault("/x/:xId/y", "/:yId",
                                                                                              false)))
                                  .map(discovery::register)
                                  .collect(Collectors.toList())).onComplete(AsyncResult::succeeded);
    }

}
