package io.zero88.qwe.http.server.mock;

import java.util.Arrays;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;

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

    @EBContract(action = "GET_LIST")
    public List<String> list(RequestData data) {
        return Arrays.asList("1", "2", "3");
    }

    @EBContract(action = "GET_ONE")
    public int get(RequestData data) {
        return Integer.parseInt(data.body().getString("event_id"));
    }

    @EBContract(action = "CREATE")
    public JsonObject create(RequestData data) {
        return new JsonObject().put("create", "success");
    }

    @EBContract(action = "UPDATE")
    public Future<String> update(RequestData data) {
        return Future.succeededFuture("success");
    }

    @EBContract(action = "PATCH")
    public Future<JsonObject> patch(RequestData data) {
        return Future.succeededFuture(new JsonObject().put("patch", "success")
                                                      .put("event_id",
                                                           Integer.valueOf(data.body().getString("event_id"))));
    }

}
