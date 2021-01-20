package io.github.zero88.msa.bp.http.server.mock;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.event.EventContractor;
import io.github.zero88.msa.bp.exceptions.CarlException;
import io.github.zero88.msa.bp.exceptions.EngineException;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;

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

    @EventContractor(action = "GET_LIST")
    public JsonObject list(RequestData data) {
        throw new RuntimeException("xxx");
    }

    @EventContractor(action = "CREATE")
    public JsonObject create(RequestData data) {
        throw new EngineException("Engine error");
    }

    @EventContractor(action = "UPDATE")
    public JsonObject update(RequestData data) {
        throw new CarlException(ErrorCode.INVALID_ARGUMENT, "invalid");
    }

}
