package io.zero88.qwe.event.refl;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.mock.MockEventListener;
import io.zero88.qwe.event.mock.MockEventListener.MockChildListener;
import io.zero88.qwe.event.mock.MockEventListener.MockDuplicateEvent;
import io.zero88.qwe.event.mock.MockEventListener.MockFuture;
import io.zero88.qwe.event.mock.MockEventListener.MockKeepEventMessageListener;
import io.zero88.qwe.event.mock.MockEventListener.MockParam;
import io.zero88.qwe.event.mock.MockEventListener.MockWithVariousParams;
import io.zero88.qwe.event.refl.EventAnnotationProcessor.MethodMeta;
import io.zero88.qwe.event.refl.EventAnnotationProcessor.MethodParam;
import io.zero88.qwe.exceptions.ImplementationError;
import io.zero88.qwe.exceptions.UnsupportedException;

class EventAnnotationProcessorTest {

    EventAnnotationProcessor processor;

    @BeforeEach
    void setup() {
        processor = EventAnnotationProcessor.create();
    }

    @Test
    void test_scan_event_not_found() {
        Assertions.assertEquals(Assertions.assertThrows(UnsupportedException.class,
                                                        () -> processor.scan(MockEventListener.class, EventAction.INIT))
                                          .getMessage(), "Unsupported event [INIT]");
    }

    @Test
    void test_scan_duplicate_event_should_failed() {
        Assertions.assertEquals(Assertions.assertThrows(ImplementationError.class,
                                                        () -> processor.scan(MockDuplicateEvent.class,
                                                                             EventAction.parse("DUP"))).getMessage(),
                                "More than one event [DUP]");
    }

    @Test
    void test_scan_no_param_success() {
        final MethodMeta methodMeta = processor.scan(MockWithVariousParams.class, EventAction.GET_LIST);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertFalse(methodMeta.outputIsVoid());
        Assertions.assertFalse(methodMeta.outputIsVertxFuture());
        Assertions.assertEquals(0, methodMeta.params().length);
    }

    @Test
    void test_scan_output_is_void() {
        final MethodMeta methodMeta = processor.scan(MockWithVariousParams.class, EventAction.NOTIFY);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals(JsonObject.class, methodMeta.params()[0].getParamClass());
        Assertions.assertTrue(methodMeta.outputIsVoid());
        Assertions.assertFalse(methodMeta.outputIsVertxFuture());
    }

    @Test
    void test_scan_event_success() {
        final MethodMeta methodMeta = processor.scan(MockEventListener.class, EventAction.CREATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertFalse(methodMeta.outputIsVoid());
        Assertions.assertFalse(methodMeta.outputIsVertxFuture());
        Assertions.assertEquals(1, methodMeta.params().length);
        final MethodParam param = methodMeta.params()[0];
        Assertions.assertNotNull(param.getParamName());
        Assertions.assertEquals(RequestData.class, param.getParamClass());
        Assertions.assertFalse(param.isContext());
    }

    @Test
    void test_scan_in_child_but_method_in_parent() {
        final MethodMeta methodMeta = processor.scan(MockChildListener.class, EventAction.CREATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertFalse(methodMeta.outputIsVoid());
        Assertions.assertFalse(methodMeta.outputIsVertxFuture());
        Assertions.assertEquals(1, methodMeta.params().length);
        final MethodParam param = methodMeta.params()[0];
        Assertions.assertNotNull(param.getParamName());
        Assertions.assertEquals(RequestData.class, param.getParamClass());
        Assertions.assertFalse(param.isContext());
    }

    @Test
    void test_scan_in_child_and_method_is_overridden() {
        final MethodMeta methodMeta = processor.scan(MockChildListener.class, EventAction.UPDATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertFalse(methodMeta.outputIsVoid());
        Assertions.assertFalse(methodMeta.outputIsVertxFuture());
        Assertions.assertEquals(1, methodMeta.params().length);
        final MethodParam param = methodMeta.params()[0];
        Assertions.assertNotNull(param.getParamName());
        Assertions.assertEquals(RequestData.class, param.getParamClass());
        Assertions.assertFalse(param.isContext());
    }

    @Test
    void test_scan_in_child_and_same_event_but_different_method_with_parent() {
        final MethodMeta methodMeta = processor.scan(MockWithVariousParams.class, EventAction.UPDATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertFalse(methodMeta.outputIsVoid());
        Assertions.assertFalse(methodMeta.outputIsVertxFuture());
        Assertions.assertEquals(2, methodMeta.params().length);
        MethodParam param1 = methodMeta.params()[0];
        Assertions.assertEquals("mock", param1.getParamName());
        Assertions.assertEquals(MockParam.class, param1.getParamClass());
        Assertions.assertFalse(param1.isContext());
        MethodParam param2 = methodMeta.params()[1];
        Assertions.assertEquals("data", param2.getParamName());
        Assertions.assertEquals(RequestData.class, param2.getParamClass());
        Assertions.assertFalse(param2.isContext());
    }

    @Test
    void test_scan_raw_event_message_value() {
        final MethodMeta methodMeta = processor.scan(MockKeepEventMessageListener.class, EventAction.MONITOR);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(2, methodMeta.params().length);
        MethodParam param1 = methodMeta.params()[0];
        Assertions.assertEquals("data", param1.getParamName());
        Assertions.assertEquals(JsonObject.class, param1.getParamClass());
        Assertions.assertFalse(param1.isContext());
        MethodParam param2 = methodMeta.params()[1];
        Assertions.assertEquals("error", param2.getParamName());
        Assertions.assertEquals(ErrorMessage.class, param2.getParamClass());
        Assertions.assertFalse(param2.isContext());
    }

    @Test
    void test_scan_raw_event_message_value_but_swap() {
        final MethodMeta methodMeta = processor.scan(MockKeepEventMessageListener.class, EventAction.NOTIFY);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(2, methodMeta.params().length);
        MethodParam param1 = methodMeta.params()[0];
        Assertions.assertEquals("error", param1.getParamName());
        Assertions.assertEquals(ErrorMessage.class, param1.getParamClass());
        Assertions.assertFalse(param1.isContext());
        MethodParam param2 = methodMeta.params()[1];
        Assertions.assertEquals("data", param2.getParamName());
        Assertions.assertEquals(JsonObject.class, param2.getParamClass());
        Assertions.assertFalse(param2.isContext());
    }

    @Test
    void test_scan_one_param_value_with_param_annotation() {
        final MethodMeta methodMeta = processor.scan(MockWithVariousParams.class, EventAction.GET_ONE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("id", methodMeta.params()[0].getParamName());
        Assertions.assertEquals(String.class, methodMeta.params()[0].getParamClass());
    }

    @Test
    void test_scan_param_as_list() {
        final MethodMeta methodMeta = processor.scan(MockWithVariousParams.class, EventAction.REMOVE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("list", methodMeta.params()[0].getParamName());
        Assertions.assertEquals(Collection.class, methodMeta.params()[0].getParamClass());
    }

    @Test
    void test_scan_output_is_future() {
        final MethodMeta methodMeta = processor.scan(MockFuture.class, EventAction.GET_ONE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("id", methodMeta.params()[0].getParamName());
        Assertions.assertEquals(int.class, methodMeta.params()[0].getParamClass());
        Assertions.assertTrue(methodMeta.outputIsVertxFuture());
        Assertions.assertFalse(methodMeta.outputIsVoid());
    }

}
