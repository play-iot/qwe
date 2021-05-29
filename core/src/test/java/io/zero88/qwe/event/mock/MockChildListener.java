package io.zero88.qwe.event.mock;

import java.util.Collections;
import java.util.Map;

import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;

public class MockChildListener extends MockEventListener {

    @EBContract(action = "UPDATE")
    public Map<String, String> update(RequestData data) {
        return Collections.singletonMap("key", "update in child");
    }

}
