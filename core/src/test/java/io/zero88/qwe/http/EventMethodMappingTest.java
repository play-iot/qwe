package io.zero88.qwe.http;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.vertx.core.http.HttpMethod;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.eventbus.EventAction;

class EventMethodMappingTest {

    @Test
    public void serialize() throws JSONException {
        final EventMethodMapping mapping = EventMethodMapping.builder()
                                                             .method(HttpMethod.CONNECT)
                                                             .action(EventAction.CREATE)
                                                             .capturePath("/abc")
                                                             .build();
        JSONAssert.assertEquals(
            "{\"action\":\"CREATE\",\"capturePath\":\"/abc\",\"regexPath\":\"/abc\",\"method\":\"CONNECT\"}",
            mapping.toJson().encode(), JSONCompareMode.STRICT);
    }

    @Test
    public void deserialize() throws JSONException {
        final EventMethodMapping from = JsonData.from(
            "{\"action\":\"REMOVE\",\"capturePath\":\"/xyz\",\"regexPath\":\"/xyz\",\"method\":\"DELETE\"}",
            EventMethodMapping.class);
        final EventMethodMapping expected = EventMethodMapping.builder()
                                                              .method(HttpMethod.DELETE)
                                                              .action(EventAction.REMOVE)
                                                              .capturePath("/xyz")
                                                              .build();
        Assertions.assertEquals(expected, from);
        JSONAssert.assertEquals(expected.toJson().encode(), from.toJson().encode(), JSONCompareMode.STRICT);
    }

}
