package io.github.zero88.msa.bp.http.server.mock;

import java.util.Arrays;
import java.util.List;

import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.event.EventContractor;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;

public class MockEventBusSuccessListener extends MockEventBusListener {

    private MockEventBusSuccessListener(EventBus eventBus, String address) {
        super(eventBus, address);
    }

    public static MockEventBusListener create(EventBus eventBus, String address) {
        return new MockEventBusSuccessListener(eventBus, address);
    }

    public static MockEventBusListener create(EventBus eventBus) {
        return new MockEventBusSuccessListener(eventBus, "http.server.test");
    }

    @EventContractor(action = "GET_LIST", returnType = List.class)
    public List<String> list(RequestData data) {
        return Arrays.asList("1", "2", "3");
    }

    @EventContractor(action = "GET_ONE", returnType = Integer.class)
    public int get(RequestData data) {
        return Integer.valueOf(data.body().getString("event_id"));
    }

    @EventContractor(action = "CREATE")
    public JsonObject create(RequestData data) {
        return new JsonObject().put("create", "success");
    }

    @EventContractor(action = "UPDATE", returnType = Single.class)
    public Single<String> update(RequestData data) {
        return Single.just("success");
    }

    @EventContractor(action = "PATCH", returnType = Single.class)
    public Single<JsonObject> patch(RequestData data) {
        return Single.just(new JsonObject().put("patch", "success")
                                           .put("event_id", Integer.valueOf(data.body().getString("event_id"))));
    }

}
