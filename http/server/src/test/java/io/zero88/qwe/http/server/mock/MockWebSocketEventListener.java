package io.zero88.qwe.http.server.mock;

import java.util.Arrays;
import java.util.List;

import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EBContract;
import io.zero88.qwe.eventbus.EventBusListener;

public class MockWebSocketEventListener implements EventBusListener {

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
