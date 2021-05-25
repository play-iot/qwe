package io.zero88.qwe.rpc.mock;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.DataTransferObject.Headers;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;

public final class MockServiceListener implements EventListener {

    @EBContract(action = "CREATE")
    public JsonObject create(RequestData requestData) {
        return new JsonObject().put("action", EventAction.CREATE.action())
                               .put(Headers.X_REQUEST_BY, requestData.headers().getString(Headers.X_REQUEST_BY));
    }

    @EBContract(action = "UPDATE")
    public JsonObject update(RequestData requestData) {
        throw new IllegalArgumentException("hey");
    }

}
