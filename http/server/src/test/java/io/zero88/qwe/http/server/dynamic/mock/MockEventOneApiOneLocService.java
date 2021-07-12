package io.zero88.qwe.http.server.dynamic.mock;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.PluginContextLookup;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.micro.DiscoveryContext;
import io.zero88.qwe.micro.DiscoveryPluginProvider;
import io.zero88.qwe.micro.RecordHelper;
import io.zero88.qwe.micro.ServiceDiscoveryApi;
import io.zero88.qwe.micro.httpevent.EventMethodDefinition;

public class MockEventOneApiOneLocService extends ApplicationVerticle {

    public String configFile() { return "eventService.json"; }

    @Override
    public void onStart() {
        addProvider(new DiscoveryPluginProvider());
        EventBusClient.create(sharedData())
                      .register(MockEventServiceListener.TEST_EVENT_1.getAddress(),
                                MockEventServiceListener.TEST_EVENT_LISTENER_1)
                      .register(MockEventServiceListener.TEST_EVENT_2.getAddress(),
                                MockEventServiceListener.TEST_EVENT_LISTENER_2)
                      .register(MockEventServiceListener.TEST_EVENT_3.getAddress(),
                                MockEventServiceListener.TEST_EVENT_LISTENER_3);
    }

    @Override
    public void onInstallCompleted(PluginContextLookup lookup) {
        publishService(Objects.requireNonNull(lookup.query(DiscoveryContext.class)));
    }

    protected void publishService(DiscoveryContext discoveryContext) {
        final ServiceDiscoveryApi discovery = discoveryContext.getDiscovery();
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
