package io.github.zero88.qwe.rpc.mock;

import java.util.Collection;

import io.github.zero88.qwe.dto.msg.DataTransferObject.Headers;
import io.github.zero88.qwe.dto.msg.RequestData;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventContractor;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.micro.http.ActionMethodMapping;
import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public final class MockServiceListener implements EventListener {

    @Override
    public @NonNull Collection<EventAction> getAvailableEvents() {
        return ActionMethodMapping.CRUD_MAP.get().keySet();
    }

    @EventContractor(action = "CREATE")
    public JsonObject create(RequestData requestData) {
        return new JsonObject().put("action", EventAction.CREATE.action())
                               .put(Headers.X_REQUEST_BY, requestData.headers().getString(Headers.X_REQUEST_BY));
    }

    @EventContractor(action = "UPDATE")
    public JsonObject update(RequestData requestData) {
        throw new IllegalArgumentException("hey");
    }

}
