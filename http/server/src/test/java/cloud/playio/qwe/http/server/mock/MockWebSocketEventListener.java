package cloud.playio.qwe.http.server.mock;

import java.util.Arrays;
import java.util.List;

import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventListener;

public class MockWebSocketEventListener implements EventListener {

    @EBContract(action = "GET_LIST")
    public List<String> list(RequestData data) {
        return Arrays.asList("1", "2", "3");
    }

    @EBContract(action = "GET_ONE")
    public String one(RequestData data) {
        return "1";
    }

    @EBContract(action = "DISCOVER")
    public String discover(RequestData data) {
        throw new IllegalArgumentException("Error");
    }

}
