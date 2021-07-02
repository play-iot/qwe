package io.zero88.qwe.http.server.dynamic.mock;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.event.EventModel;
import io.zero88.qwe.micro.DiscoveryContext;
import io.zero88.qwe.micro.RecordHelper;
import io.zero88.qwe.micro.ServiceDiscoveryApi;

public class SimilarApiService extends MockEventOneApiOneLocService {

    static EventModel EVENT_1 = EventModel.builder()
                                          .address("test.SimilarApiService.1")
                                          .local(true)
                                          .pattern(EventPattern.REQUEST_RESPONSE)
                                          .addEvents(EventAction.GET_ONE, EventAction.GET_LIST)
                                          .build();
    static EventModel EVENT_2 = EventModel.clone(EVENT_1, "test.SimilarApiService.2");

    @Override
    public void onStart() {
        getEventBus().register(EVENT_1.getAddress(), new MockSiteListener())
                     .register(EVENT_2.getAddress(), new MockProductListener());
    }

    @Override
    protected void publishService(DiscoveryContext discoveryContext) {
        final ServiceDiscoveryApi discovery = discoveryContext.getDiscovery();
        CompositeFuture.all(Stream.of(RecordHelper.create("ems-5", EVENT_1.getAddress(),
                                                          EventMethodDefinition.createDefault("/client/:cId/site",
                                                                                              "/:sId")),
                                      RecordHelper.create("ems-5", EVENT_2.getAddress(),
                                                          EventMethodDefinition.createDefault(
                                                              "/client/:cId/site/:sId/product", "/:pId")))
                                  .map(discovery::register)
                                  .collect(Collectors.toList())).onComplete(AsyncResult::succeeded);
    }

    static class MockSiteListener implements EventListener {

        @EBContract(action = "GET_LIST")
        public JsonObject list(RequestData data) {
            return new JsonObject().put("from", "GET_LIST From site");
        }

        @EBContract(action = "GET_ONE")
        public JsonObject get(RequestData data) {
            return new JsonObject().put("from", "GET_ONE From site");
        }

    }


    static class MockProductListener implements EventListener {

        @EBContract(action = "GET_LIST")
        public JsonObject list(RequestData data) {
            return new JsonObject().put("from", "GET_LIST From product");
        }

        @EBContract(action = "GET_ONE")
        public JsonObject get(RequestData data) {
            return new JsonObject().put("from", "GET_ONE From product");
        }

    }

}
