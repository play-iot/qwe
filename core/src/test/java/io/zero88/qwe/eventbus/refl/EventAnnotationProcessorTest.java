package io.zero88.qwe.eventbus.refl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EBBody;
import io.zero88.qwe.eventbus.EventAction;
import io.zero88.qwe.eventbus.mock.MockChildListener;
import io.zero88.qwe.eventbus.mock.MockEventListener;
import io.zero88.qwe.eventbus.mock.MockFailedListener;
import io.zero88.qwe.eventbus.mock.MockFutureListener;
import io.zero88.qwe.eventbus.mock.MockKeepEventMessageListener;
import io.zero88.qwe.eventbus.mock.MockParam;
import io.zero88.qwe.eventbus.mock.MockWithVariousParamsListener;
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
                                                        () -> processor.lookup(MockEventListener.class,
                                                                               EventAction.INIT)).getMessage(),
                                "Unsupported event [INIT]");
    }

    @Test
    void test_scan_duplicate_event_should_failed() {
        Assertions.assertEquals(Assertions.assertThrows(ImplementationError.class,
                                                        () -> processor.lookup(MockFailedListener.class,
                                                                               EventAction.parse("DUP"))).getMessage(),
                                "More than one event [DUP]");
    }

    @Test
    void test_scan_no_param_success() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.GET_LIST);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(0, methodMeta.params().length);
    }

    @Test
    void test_scan_output_is_void() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.NOTIFY);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals(JsonObject.class, methodMeta.params()[0].getParamClass());
    }

    @Test
    void test_scan_event_success() {
        final MethodMeta methodMeta = processor.lookup(MockEventListener.class, EventAction.CREATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        final MethodParam param = methodMeta.params()[0];
        Assertions.assertNotNull(param.getParamName());
        Assertions.assertEquals(RequestData.class, param.getParamClass());
        Assertions.assertFalse(param.isEBContext());
    }

    @Test
    void test_scan_in_child_but_method_in_parent() {
        final MethodMeta methodMeta = processor.lookup(MockChildListener.class, EventAction.CREATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(MockEventListener.class.getName(), methodMeta.declaringClass());
        Assertions.assertEquals(1, methodMeta.params().length);
        final MethodParam param = methodMeta.params()[0];
        Assertions.assertNotNull(param.getParamName());
        Assertions.assertEquals(RequestData.class, param.getParamClass());
        Assertions.assertFalse(param.isEBContext());
    }

    @Test
    void test_scan_in_child_and_method_is_overridden() {
        final MethodMeta methodMeta = processor.lookup(MockChildListener.class, EventAction.UPDATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(MockChildListener.class.getName(), methodMeta.declaringClass());
        Assertions.assertEquals(1, methodMeta.params().length);
        final MethodParam param = methodMeta.params()[0];
        Assertions.assertNotNull(param.getParamName());
        Assertions.assertEquals(RequestData.class, param.getParamClass());
        Assertions.assertFalse(param.isEBContext());
    }

    @Test
    void test_scan_in_child_and_same_event_but_different_method_with_parent() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.UPDATE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(MockWithVariousParamsListener.class.getName(), methodMeta.declaringClass());
        Assertions.assertEquals(2, methodMeta.params().length);
        MethodParam param1 = methodMeta.params()[0];
        Assertions.assertEquals("mock", param1.getParamName());
        Assertions.assertEquals(MockParam.class, param1.getParamClass());
        Assertions.assertFalse(param1.isEBContext());
        MethodParam param2 = methodMeta.params()[1];
        Assertions.assertEquals("data", param2.getParamName());
        Assertions.assertEquals(RequestData.class, param2.getParamClass());
        Assertions.assertFalse(param2.isEBContext());
    }

    @Test
    void test_scan_raw_event_message_value() {
        final MethodMeta methodMeta = processor.lookup(MockKeepEventMessageListener.class, EventAction.MONITOR);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(2, methodMeta.params().length);
        MethodParam param1 = methodMeta.params()[0];
        Assertions.assertEquals("data", param1.getParamName());
        Assertions.assertEquals(JsonObject.class, param1.getParamClass());
        Assertions.assertFalse(param1.isEBContext());
        MethodParam param2 = methodMeta.params()[1];
        Assertions.assertEquals("error", param2.getParamName());
        Assertions.assertEquals(ErrorMessage.class, param2.getParamClass());
        Assertions.assertFalse(param2.isEBContext());
    }

    @Test
    void test_scan_raw_event_message_value_but_swap() {
        final MethodMeta methodMeta = processor.lookup(MockKeepEventMessageListener.class, EventAction.NOTIFY);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(2, methodMeta.params().length);
        MethodParam param1 = methodMeta.params()[0];
        Assertions.assertEquals("error", param1.getParamName());
        Assertions.assertEquals(ErrorMessage.class, param1.getParamClass());
        Assertions.assertFalse(param1.isEBContext());
        MethodParam param2 = methodMeta.params()[1];
        Assertions.assertEquals("data", param2.getParamName());
        Assertions.assertEquals(JsonObject.class, param2.getParamClass());
        Assertions.assertFalse(param2.isEBContext());
    }

    @Test
    void test_scan_one_param_value_with_param_annotation() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.GET_ONE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("id", methodMeta.params()[0].getParamName());
        Assertions.assertFalse(methodMeta.params()[0].isEBContext());
        Assertions.assertFalse(methodMeta.params()[0].isCollection());
        Assertions.assertNull(((MethodParamInternal) methodMeta.params()[0]).getArrayOrCollectionComponentType());
        Assertions.assertEquals(String.class, methodMeta.params()[0].getParamClass());
    }

    @Test
    void test_scan_output_is_future() {
        final MethodMeta methodMeta = processor.lookup(MockFutureListener.class, EventAction.GET_ONE);
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("id", methodMeta.params()[0].getParamName());
        Assertions.assertFalse(methodMeta.params()[0].isEBContext());
        Assertions.assertFalse(methodMeta.params()[0].isCollection());
        Assertions.assertNull(((MethodParamInternal) methodMeta.params()[0]).getArrayOrCollectionComponentType());
        Assertions.assertEquals(int.class, methodMeta.params()[0].getParamClass());
    }

    @Test
    void test_scan_param_as_list() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.parse("LIST"));
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("list", methodMeta.params()[0].getParamName());
        Assertions.assertTrue(methodMeta.params()[0].isCollection());
        Assertions.assertEquals(List.class, methodMeta.params()[0].getParamClass());
        Assertions.assertEquals(String.class,
                                ((MethodParamInternal) methodMeta.params()[0]).getArrayOrCollectionComponentType());
    }

    @Test
    void test_scan_param_as_set() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.parse("SET"));
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        final EBBody ebBody = methodMeta.params()[0].lookupAnnotation(EBBody.class);
        Assertions.assertNotNull(ebBody);
        Assertions.assertEquals("set", ebBody.value());
        Assertions.assertTrue(methodMeta.params()[0].isCollection());
        Assertions.assertEquals(Set.class, methodMeta.params()[0].getParamClass());
        Assertions.assertEquals(MockParam.class,
                                ((MethodParamInternal) methodMeta.params()[0]).getArrayOrCollectionComponentType());
    }

    @Test
    void test_scan_param_as_map() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.parse("MAP"));
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        final EBBody ebBody = methodMeta.params()[0].lookupAnnotation(EBBody.class);
        Assertions.assertNotNull(ebBody);
        Assertions.assertEquals("map", ebBody.value());
        Assertions.assertFalse(methodMeta.params()[0].isCollection());
        Assertions.assertNull(((MethodParamInternal) methodMeta.params()[0]).getArrayOrCollectionComponentType());
        Assertions.assertEquals(Map.class, methodMeta.params()[0].getParamClass());
        final Class<?>[] types = ((MethodParamInternal) methodMeta.params()[0]).getMapComponentType();
        Assertions.assertNotNull(types);
        Assertions.assertEquals(String.class, types[0]);
        Assertions.assertEquals(MockParam.class, types[1]);
    }

    @Test
    void test_scan_param_as_array() {
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, EventAction.parse("ARRAY"));
        Assertions.assertNotNull(methodMeta);
        Assertions.assertEquals(1, methodMeta.params().length);
        Assertions.assertEquals("array", methodMeta.params()[0].getParamName());
        Assertions.assertFalse(methodMeta.params()[0].isCollection());
        Assertions.assertTrue(methodMeta.params()[0].isArray());
        Assertions.assertEquals(MockParam.class,
                                ((MethodParamInternal) methodMeta.params()[0]).getArrayOrCollectionComponentType());
    }

}
