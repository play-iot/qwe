package io.zero88.qwe.event.mock;

import java.util.Collections;
import java.util.Map;

import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventListener;

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
