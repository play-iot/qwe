package cloud.playio.qwe.micro.mock;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.micro.GatewayHeaders;

public final class MockServiceListener implements EventListener {

    @EBContract(action = "CREATE")
    public JsonObject create(RequestData requestData) {
        return new JsonObject().put("action", EventAction.CREATE.action())
                               .put(GatewayHeaders.X_REQUEST_BY,
                                    requestData.headers().getString(GatewayHeaders.X_REQUEST_BY));
    }

    @EBContract(action = "UPDATE")
    public JsonObject update(RequestData requestData) {
        throw new IllegalArgumentException("hey");
    }

}
