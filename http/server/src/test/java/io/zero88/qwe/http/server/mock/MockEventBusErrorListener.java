package io.zero88.qwe.http.server.mock;

import io.github.zero88.exceptions.ErrorCode;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.EngineException;

public class MockEventBusErrorListener extends MockEventBusListener {

    private MockEventBusErrorListener(EventBus eventBus, String address) {
        super(eventBus, address);
    }

    public static MockEventBusListener create(EventBus eventBus, String address) {
        return new MockEventBusErrorListener(eventBus, address);
    }

    public static MockEventBusListener create(EventBus eventBus) {
        return new MockEventBusErrorListener(eventBus, "http.server.test");
    }

    @EBContract(action = "GET_LIST")
    public JsonObject list(RequestData data) {
        throw new RuntimeException("xxx");
    }

    @EBContract(action = "CREATE")
    public JsonObject create(RequestData data) {
        throw new EngineException("Engine error");
    }

    @EBContract(action = "UPDATE")
    public JsonObject update(RequestData data) {
        throw new QWEException(ErrorCode.INVALID_ARGUMENT, "invalid");
    }

}
