package cloud.playio.qwe.http.server.proxy.mock;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import cloud.playio.qwe.ApplicationVerticle;
import cloud.playio.qwe.ApplicationContextHolder;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.micro.DiscoveryContext;
import cloud.playio.qwe.micro.DiscoveryPluginProvider;
import cloud.playio.qwe.micro.RecordHelper;
import cloud.playio.qwe.micro.ServiceDiscoveryApi;
import cloud.playio.qwe.http.EventMethodDefinition;

public class MockEventOneApiOneLocService extends ApplicationVerticle {

    public String configFile() { return "eventService.json"; }

    @Override
    public void onStart() {
        addProvider(new DiscoveryPluginProvider());
        EventBusClient.create(sharedData())
                      .register(MockEventServiceListener.TEST_EVENT_1_ADDR,
                                MockEventServiceListener.TEST_EVENT_LISTENER_1)
                      .register(MockEventServiceListener.TEST_EVENT_2_ADDR,
                                MockEventServiceListener.TEST_EVENT_LISTENER_2)
                      .register(MockEventServiceListener.TEST_EVENT_3_ADDR,
                                MockEventServiceListener.TEST_EVENT_LISTENER_3);
    }

    @Override
    public void onInstallCompleted(ApplicationContextHolder holder) {
        publishService(Objects.requireNonNull(holder.plugin(DiscoveryContext.class)));
    }

    protected void publishService(DiscoveryContext discoveryContext) {
        final ServiceDiscoveryApi discovery = discoveryContext.getDiscovery();
        CompositeFuture.all(Stream.of(RecordHelper.create("ems-1", MockEventServiceListener.TEST_EVENT_1_ADDR,
                                                          EventMethodDefinition.createDefault("/hey", "/:id")),
                                      RecordHelper.create("ems-2", MockEventServiceListener.TEST_EVENT_2_ADDR,
                                                          EventMethodDefinition.createDefault("/c/:cId/p", "/:pId")),
                                      RecordHelper.create("ems-3", MockEventServiceListener.TEST_EVENT_3_ADDR,
                                                          EventMethodDefinition.createDefault("/x/:xId/y", "/:yId",
                                                                                              false)))
                                  .map(discovery::register)
                                  .collect(Collectors.toList())).onComplete(AsyncResult::succeeded);
    }

}
