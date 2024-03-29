package cloud.playio.qwe.eventbus.mock;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.dto.ErrorMessage;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventListener;

public class MockKeepEventMessageListener implements EventListener {

    @EBContract(action = "MONITOR")
    public int monitor(@EBParam("data") JsonObject json, @EBParam("error") ErrorMessage err) {
        return Objects.isNull(err) ? 1 : 0;
    }

    @EBContract(action = "NOTIFY")
    public int swap(@EBParam("error") ErrorMessage error, @EBParam("data") JsonObject data) {
        return Objects.isNull(error) ? 1 : 0;
    }

}
