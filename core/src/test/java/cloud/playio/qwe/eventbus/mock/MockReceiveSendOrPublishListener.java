package cloud.playio.qwe.eventbus.mock;

import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EBParam;
import cloud.playio.qwe.eventbus.EventListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockReceiveSendOrPublishListener implements EventListener {

    private final String identifier;
    private final Checkpoint cp;

    @EBContract(action = "GET_ONE")
    public JsonObject receive(@EBParam("id") int id) {
        cp.flag();
        System.out.println("[" + identifier + "] receive event");
        return new JsonObject().put("id", id).put("identifier", identifier);
    }

}
