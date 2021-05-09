package io.zero88.qwe.event;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.classgraph.MethodInfo;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.mock.MockEventListener;
import io.zero88.qwe.event.mock.MockEventListener.MockChildListener;
import io.zero88.qwe.event.mock.MockEventListener.MockKeepEventMessageListener;
import io.zero88.qwe.event.mock.MockEventListener.MockParam;
import io.zero88.qwe.event.mock.MockEventListener.MockWithVariousParams;
import io.zero88.qwe.exceptions.UnsupportedException;

class EventAnnotationProcessorTest {

    private EventAnnotationProcessor processor;

    @BeforeEach
    void setup() {
        processor = EventAnnotationProcessor.create();
    }

    @AfterEach
    void teardown() {
        processor.close();
    }

    @Test
    void test_find_success() {
        Assertions.assertNotNull(processor.find(MockEventListener.class, EventAction.CREATE));
    }

    @Test
    void test_find_not_found() {
        Assertions.assertEquals(Assertions.assertThrows(UnsupportedException.class,
                                                        () -> processor.find(MockEventListener.class, EventAction.INIT))
                                          .getMessage(), "Unsupported event [INIT]");
    }

    @Test
    void test_find_child() {
        Assertions.assertNotNull(processor.find(MockChildListener.class, EventAction.CREATE));
        Assertions.assertNotNull(processor.find(MockChildListener.class, EventAction.UPDATE));
    }

    @Test
    void test_extract_one_param_value() {
        final RequestData reqData = RequestData.builder()
                                               .filter(new JsonObject().put("ab", "cd"))
                                               .body(new JsonObject().put("xy", 123))
                                               .build();
        final EventMessage msg = EventMessage.initial(EventAction.CREATE, reqData);
        final MethodInfo methodInfo = processor.find(MockChildListener.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(1, paramValues.length);
        final Object o = paramValues[0];
        Assertions.assertTrue(o instanceof RequestData);
        Assertions.assertEquals(reqData.filter(), ((RequestData) o).filter());
        Assertions.assertEquals(reqData.body(), ((RequestData) o).body());
    }

    @Test
    void test_extract_raw_event_message_value() {
        final RequestData reqData = RequestData.builder()
                                               .filter(new JsonObject().put("ab", "cd"))
                                               .body(new JsonObject().put("xy", 123))
                                               .build();
        final EventMessage msg = EventMessage.initial(EventAction.MONITOR, reqData);
        final MethodInfo methodInfo = processor.find(MockKeepEventMessageListener.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(2, paramValues.length);
        Assertions.assertEquals(reqData.toJson(), paramValues[0]);
        Assertions.assertNull(paramValues[1]);
    }

    @Test
    void test_extract_raw_event_message_value_but_swap() {
        final RequestData reqData = RequestData.builder()
                                               .filter(new JsonObject().put("ab", "cd"))
                                               .body(new JsonObject().put("xy", 123))
                                               .build();
        final EventMessage msg = EventMessage.initial(EventAction.NOTIFY, reqData);
        final MethodInfo methodInfo = processor.find(MockKeepEventMessageListener.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(2, paramValues.length);
        Assertions.assertNull(paramValues[0]);
        Assertions.assertEquals(reqData.toJson(), paramValues[1]);
    }

    @Test
    void test_extract_no_param() {
        final EventMessage msg = EventMessage.initial(EventAction.GET_LIST);
        final MethodInfo methodInfo = processor.find(MockWithVariousParams.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(0, paramValues.length);
    }

    @Test
    void test_extract_one_param_value_with_param_annotation() {
        final EventMessage msg = EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", "10"));
        final MethodInfo methodInfo = processor.find(MockWithVariousParams.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(1, paramValues.length);
        Assertions.assertEquals("10", paramValues[0]);
    }

    @Test
    void test_extract_2_param_value_with_param_annotation() {
        final RequestData reqData = RequestData.builder().body(new JsonObject().put("xy", 123)).build();
        final MockParam mockParam = new MockParam(10, "name");
        final EventMessage msg = EventMessage.initial(EventAction.UPDATE,
                                                      new JsonObject().put("mock", mockParam).put("data", reqData));
        final MethodInfo methodInfo = processor.find(MockWithVariousParams.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(2, paramValues.length);
        Assertions.assertEquals(mockParam, paramValues[0]);
        Assertions.assertEquals(reqData.toJson(), ((RequestData) paramValues[1]).toJson());
    }

    @Test
    void test_extract_param_as_list() {
        final EventMessage msg = EventMessage.initial(EventAction.HALT,
                                                      new JsonObject().put("list", Arrays.asList(1, 2, 3)));
        final MethodInfo methodInfo = processor.find(MockWithVariousParams.class, msg.getAction());
        Assertions.assertNotNull(methodInfo);
        final Object[] paramValues = processor.extract(JsonData.MAPPER, msg, methodInfo.getParameterInfo());
        Assertions.assertNotNull(paramValues);
        Assertions.assertEquals(1, paramValues.length);
        Assertions.assertEquals(Arrays.asList(1, 2, 3), paramValues[0]);
    }

}
