package io.github.zero88.qwe.http.server.dynamic.mock;

import java.util.Collection;

import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.micro.MicroContext;
import io.github.zero88.qwe.micro.ServiceDiscoveryController;
import io.github.zero88.qwe.micro.metadata.ActionMethodMapping;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public class SimilarApiService extends MockEventOneApiOneLocService {

    static EventModel EVENT_1 = EventModel.builder()
                                          .address("test.SimilarApiService.1")
                                          .local(true)
                                          .pattern(EventPattern.REQUEST_RESPONSE)
                                          .addEvents(EventAction.GET_ONE, EventAction.GET_LIST)
                                          .build();
    static EventModel EVENT_2 = EventModel.clone(EVENT_1, "test.SimilarApiService.2");

    @Override
    public void registerEventbus(EventbusClient eventbus) {
        eventbus.register(EVENT_1, new MockSiteListener()).register(EVENT_2, new MockProductListener());
    }

    @Override
    protected void publishService(MicroContext microContext) {
        final ServiceDiscoveryController controller = microContext.getLocalController();
        Single.concat(controller.addEventMessageRecord("ems-5", EVENT_1.getAddress(),
                                                       EventMethodDefinition.createDefault("/client/:cId/site",
                                                                                           "/:sId")),
                      controller.addEventMessageRecord("ems-5", EVENT_2.getAddress(),
                                                       EventMethodDefinition.createDefault(
                                                           "/client/:cId/site/:sId/product", "/:pId"))).subscribe();
    }

    static class MockSiteListener implements EventListener {

        @Override
        public @NonNull Collection<EventAction> getAvailableEvents() {
            return ActionMethodMapping.DQL_MAP.get().keySet();
        }

        @EventContractor(action = "GET_LIST")
        public JsonObject list(RequestData data) {
            return new JsonObject().put("from", "GET_LIST From site");
        }

        @EventContractor(action = "GET_ONE")
        public JsonObject get(RequestData data) {
            return new JsonObject().put("from", "GET_ONE From site");
        }

    }


    static class MockProductListener implements EventListener {

        @Override
        public @NonNull Collection<EventAction> getAvailableEvents() {
            return ActionMethodMapping.DQL_MAP.get().keySet();
        }

        @EventContractor(action = "GET_LIST")
        public JsonObject list(RequestData data) {
            return new JsonObject().put("from", "GET_LIST From product");
        }

        @EventContractor(action = "GET_ONE")
        public JsonObject get(RequestData data) {
            return new JsonObject().put("from", "GET_ONE From product");
        }

    }

}
