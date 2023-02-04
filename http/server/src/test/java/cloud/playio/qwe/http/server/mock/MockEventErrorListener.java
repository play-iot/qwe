package cloud.playio.qwe.http.server.mock;

import io.github.zero88.exceptions.ErrorCode;
import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.exceptions.EngineException;
import cloud.playio.qwe.exceptions.QWEException;

public class MockEventErrorListener implements EventListener {

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
