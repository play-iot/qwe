package cloud.playio.qwe.eventbus.refl;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.eventbus.EBBody;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventListenerTest;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.eventbus.mock.MockEventListener;
import cloud.playio.qwe.eventbus.mock.MockParam;
import cloud.playio.qwe.eventbus.mock.MockWithContextListener;
import cloud.playio.qwe.eventbus.mock.MockWithVariousParamsListener;

@ExtendWith(VertxExtension.class)
class EventParameterParserTest {

    EventAnnotationProcessor processor;
    EventParameterParser parser;

    @BeforeEach
    void setup(Vertx vertx) {
        processor = EventAnnotationProcessor.create();
        parser = EventParameterParser.create(SharedDataLocalProxy.create(vertx, EventListenerTest.class.getName()),
                                             JsonData.MAPPER);
    }

    @Test
    void test_extract_primitive_but_send_null() {
        final EventMessage msg = EventMessage.initial(MockEventListener.PRIMITIVE_EVENT,
                                                      new JsonObject().put("id", null));
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        Assertions.assertEquals(1, meta.params().length);
        Assertions.assertEquals("Data Field [id] is primitive type but given null data",
                                Assertions.assertThrows(IllegalArgumentException.class,
                                                        () -> parser.extract(msg, meta.params())).getMessage());
    }

    @Test
    void test_extract_string() {
        final EventMessage msg = EventMessage.initial(EventAction.GET_ONE, new JsonObject().put("id", "123"));
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertEquals("123", inputs[0]);
    }

    @Test
    void test_extract_all_context() {
        final RequestData reqData = RequestData.builder().body(new JsonObject().put("id", "123")).build();
        final EventMessage msg = EventMessage.initial(EventAction.GET_ONE, reqData);
        final MethodMeta meta = processor.lookup(MockWithContextListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(5, inputs.length);
        Assertions.assertEquals(EventAction.GET_ONE, inputs[0]);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[1].getClass(), Vertx.class));
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[2].getClass(), SharedDataLocalProxy.class));
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[3].getClass(), EventBusClient.class));
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[4].getClass(), RequestData.class));
        Assertions.assertEquals(reqData.toJson(), ((RequestData) inputs[4]).toJson());
    }

    @Test
    void test_extract_mixin_context_param_position() {
        final RequestData reqData = RequestData.builder().filter(new JsonObject().put("test", 1)).build();
        final EventMessage msg = EventMessage.initial(EventAction.PATCH, reqData);
        final MethodMeta meta = processor.lookup(MockWithContextListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(4, inputs.length);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[0].getClass(), RequestFilter.class));
        Assertions.assertEquals(new RequestFilter(reqData.filter().toJson()).toJson(),
                                ((RequestFilter) inputs[0]).toJson());
        Assertions.assertEquals(EventAction.PATCH, inputs[1]);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[2].getClass(), EventBusClient.class));
        Assertions.assertNull(inputs[3]);
        Assertions.assertEquals("body", meta.params()[3].getParamName());
        Assertions.assertEquals(JsonObject.class, meta.params()[3].getParamClass());
    }

    @Test
    void test_register_many_actions_then_inject_correct_runtime_action() {
        final EventMessage msg1 = EventMessage.initial(EventAction.CREATE, new JsonObject().put("id", 111));
        final MethodMeta meta1 = processor.lookup(MockWithContextListener.class, msg1.getAction());
        final Object[] inputs1 = parser.extract(msg1, meta1.params());
        Assertions.assertEquals(2, inputs1.length);
        Assertions.assertEquals(EventAction.CREATE, inputs1[0]);
        Assertions.assertEquals(111, inputs1[1]);

        final EventMessage msg2 = EventMessage.initial(EventAction.UPDATE, new JsonObject().put("id", 222));
        final MethodMeta meta2 = processor.lookup(MockWithContextListener.class, msg2.getAction());
        final Object[] inputs2 = parser.extract(msg2, meta2.params());
        Assertions.assertEquals(2, inputs2.length);
        Assertions.assertEquals(EventAction.UPDATE, inputs2[0]);
        Assertions.assertEquals(222, inputs2[1]);
    }

    @Test
    void test_extract_body_full_and_headers() {
        final RequestData reqData = RequestData.builder()
                                               .body(new JsonObject().put("id", 2))
                                               .headers(new JsonObject().put("hello", "zero88"))
                                               .build();
        final EventMessage msg = EventMessage.initial(EventAction.parse("BODY_FULL"), reqData);
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(2, inputs.length);

        final EBBody annotation = meta.params()[0].lookupAnnotation(EBBody.class);
        Assertions.assertNotNull(annotation);
        Assertions.assertEquals("", annotation.value());
        Assertions.assertEquals(JsonObject.class, meta.params()[0].getParamClass());
        Assertions.assertEquals(new JsonObject().put("id", 2), inputs[0]);

        Assertions.assertEquals("headers", meta.params()[1].getParamName());
        Assertions.assertEquals(JsonObject.class, meta.params()[1].getParamClass());
        Assertions.assertEquals(new JsonObject().put("hello", "zero88"), inputs[1]);
    }

    @Test
    void test_extract_body_part_and_headers() {
        final RequestData reqData = RequestData.builder()
                                               .body(new JsonObject().put("id", 1))
                                               .headers(new JsonObject().put("hello", "world"))
                                               .build();
        final EventMessage msg = EventMessage.initial(EventAction.parse("BODY_PART"), reqData);
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(2, inputs.length);

        final EBBody annotation = meta.params()[0].lookupAnnotation(EBBody.class);
        Assertions.assertNotNull(annotation);
        Assertions.assertEquals("id", annotation.value());
        Assertions.assertEquals(Integer.class, meta.params()[0].getParamClass());
        Assertions.assertEquals(1, inputs[0]);

        Assertions.assertEquals("headers", meta.params()[1].getParamName());
        Assertions.assertEquals(JsonObject.class, meta.params()[1].getParamClass());
        Assertions.assertEquals(new JsonObject().put("hello", "world"), inputs[1]);
    }

    @Test
    void test_use_EBBody_but_non_standard_message() {
        final EventMessage msg = EventMessage.initial(EventAction.parse("BODY_PART"), new JsonObject().put("id", 2));
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(2, inputs.length);

        final EBBody annotation = meta.params()[0].lookupAnnotation(EBBody.class);
        Assertions.assertNotNull(annotation);
        Assertions.assertEquals(Integer.class, meta.params()[0].getParamClass());
        Assertions.assertEquals(2, inputs[0]);

        Assertions.assertEquals("headers", meta.params()[1].getParamName());
        Assertions.assertEquals(JsonObject.class, meta.params()[1].getParamClass());
        Assertions.assertNull(inputs[1]);
    }

    @Test
    void test_extract_param_as_list() {
        final JsonObject data = new JsonObject().put("list", new JsonArray().add("abc").add("xyz"));
        final EventMessage msg = EventMessage.initial(EventAction.parse("LIST"), data);
        final MethodMeta methodMeta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, methodMeta.params());
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[0].getClass(), List.class));
        Assertions.assertEquals(2, ((List) inputs[0]).size());
        Assertions.assertTrue(
            ReflectionClass.assertDataType(String.class, ((List) inputs[0]).stream().findFirst().get().getClass()));
        Assertions.assertEquals(Arrays.asList("abc", "xyz"), inputs[0]);
    }

    @Test
    void test_extract_body_as_set() {
        final RequestData reqData = RequestData.builder()
                                               .body(new JsonObject().put("set", Collections.singleton(
                                                   JsonData.tryParse(new MockParam(1, "abc")).toJson())))
                                               .build();
        final EventMessage msg = EventMessage.initial(EventAction.parse("SET"), reqData);
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[0].getClass(), Set.class));
        Assertions.assertEquals(1, ((Set) inputs[0]).size());
        Assertions.assertTrue(
            ReflectionClass.assertDataType(MockParam.class, ((Set) inputs[0]).stream().findFirst().get().getClass()));
    }

    @Test
    void test_extract_body_as_collection() {
        final JsonObject data = new JsonObject().put("collection", Collections.singleton(
            JsonData.tryParse(new MockParam(1, "abc")).toJson()));
        final EventMessage msg = EventMessage.initial(EventAction.parse("COLLECTION"), data);
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertTrue(ReflectionClass.assertDataType(meta.params()[0].getParamClass(), Collection.class));
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[0].getClass(), Collection.class));
        Assertions.assertEquals(1, ((Collection) inputs[0]).size());
        Assertions.assertTrue(ReflectionClass.assertDataType(MockParam.class, ((Collection) inputs[0]).stream()
                                                                                                      .findFirst()
                                                                                                      .get()
                                                                                                      .getClass()));
    }

    @Test
    void test_extract_param_as_array() {
        final JsonObject data = new JsonObject().put("array", new JsonArray().add(
            JsonData.tryParse(new MockParam(1, "abc")).toJson()));
        final EventMessage msg = EventMessage.initial(EventAction.parse("ARRAY"), data);
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertTrue(inputs[0].getClass().isArray());
        Assertions.assertEquals(MockParam.class, inputs[0].getClass().getComponentType());
        Assertions.assertEquals(1, Array.getLength(inputs[0]));
        Assertions.assertTrue(ReflectionClass.assertDataType(MockParam.class, Array.get(inputs[0], 0).getClass()));
        Assertions.assertEquals(1, ((MockParam) Array.get(inputs[0], 0)).getId());
        Assertions.assertEquals("abc", ((MockParam) Array.get(inputs[0], 0)).getName());
    }

    @Test
    void test_extract_body_as_map() {
        final JsonObject data = new JsonObject().put("map", Collections.singletonMap("x", JsonData.tryParse(
            new MockParam(1, "abc")).toJson()));
        final EventMessage msg = EventMessage.initial(EventAction.parse("MAP"), data);
        final MethodMeta meta = processor.lookup(MockWithVariousParamsListener.class, msg.getAction());
        final Object[] inputs = parser.extract(msg, meta.params());
        Assertions.assertTrue(ReflectionClass.assertDataType(meta.params()[0].getParamClass(), Map.class));
        Assertions.assertEquals(1, inputs.length);
        Assertions.assertTrue(ReflectionClass.assertDataType(inputs[0].getClass(), Map.class));
        Assertions.assertEquals(1, ((Map) inputs[0]).size());
        final Entry entry = (Entry) ((Map) inputs[0]).entrySet().stream().findFirst().get();
        Assertions.assertTrue(ReflectionClass.assertDataType(String.class, entry.getKey().getClass()));
        Assertions.assertEquals("x", entry.getKey());
        Assertions.assertTrue(ReflectionClass.assertDataType(MockParam.class, entry.getValue().getClass()));
        Assertions.assertEquals(1, ((MockParam)entry.getValue()).getId());
        Assertions.assertEquals("abc", ((MockParam)entry.getValue()).getName());
    }

}
