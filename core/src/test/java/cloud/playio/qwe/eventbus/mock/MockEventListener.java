package cloud.playio.qwe.eventbus.mock;

import java.util.Collections;
import java.util.Map;

import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventListener;

public class MockEventListener implements EventListener {

    public static final EventAction ERROR_EVENT = EventAction.parse("ERR");
    public static final EventAction PRIMITIVE_EVENT = EventAction.parse("PRIMITIVE");

    @EBContract(action = "CREATE")
    public Map<String, String> create(RequestData data) {
        return Collections.singletonMap("key", "install");
    }

    @EBContract(action = "UPDATE")
    public Map<String, String> update(RequestData data) {
        return Collections.singletonMap("key", "update");
    }

}
