package cloud.playio.qwe.eventbus.mock;

import java.util.Collections;
import java.util.Map;

import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;

public class MockChildListener extends MockEventListener {

    @EBContract(action = "UPDATE")
    public Map<String, String> update(RequestData data) {
        return Collections.singletonMap("key", "update in child");
    }

}
