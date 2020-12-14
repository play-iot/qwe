package io.github.zero88.msa.bp.http.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.exceptions.InvalidUrlException;
import io.github.zero88.msa.bp.dto.JsonData;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.exceptions.ServiceNotFoundException;
import io.vertx.core.http.HttpMethod;


public class EventMethodDefinitionTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_default_wrong_argument_1() {
        EventMethodDefinition.createDefault("", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_default_wrong_argument_2() {
        EventMethodDefinition.createDefault("/abc", "");
    }

    @Test(expected = InvalidUrlException.class)
    public void test_same_capture() {
        EventMethodDefinition.createDefault("abc", "/abc");
    }

    @Test(expected = IllegalStateException.class)
    public void test_create_duplicate() {
        ActionMethodMapping mapping = () -> {
            Map<EventAction, HttpMethod> test = new HashMap<>();
            test.put(EventAction.GET_LIST, HttpMethod.GET);
            test.put(EventAction.GET_ONE, HttpMethod.GET);
            return test;
        };
        EventMethodDefinition.create("/abc", mapping);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_wrong_multiParam_pattern() {
        EventMethodDefinition.createDefault("/client/:clientId", "/:productId");
    }

    @Test(expected = ServiceNotFoundException.class)
    public void test_not_found() {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/abc", "/:id");
        definition.search("/abcd", HttpMethod.GET);
    }

    @Test(expected = ServiceNotFoundException.class)
    public void test_not_found_multiParam_pattern() {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/abc/:cid/prod", "/:pid");
        definition.search("/abc/222/xyz/prod", HttpMethod.GET);
    }

    @Test
    public void test_no_capture() {
        ActionMethodMapping mapping = () -> {
            Map<EventAction, HttpMethod> test = new HashMap<>();
            test.put(EventAction.GET_LIST, HttpMethod.GET);
            test.put(EventAction.CREATE, HttpMethod.POST);
            test.put(EventAction.UPDATE, HttpMethod.PUT);
            test.put(EventAction.PATCH, HttpMethod.PATCH);
            test.put(EventAction.REMOVE, HttpMethod.DELETE);
            return test;
        };
        EventMethodDefinition definition = EventMethodDefinition.create("/translate", mapping);
        Assert.assertTrue(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_LIST, definition.search("/translate", HttpMethod.GET));
        Assert.assertEquals(EventAction.CREATE, definition.search("/translate", HttpMethod.POST));
        Assert.assertEquals(EventAction.UPDATE, definition.search("/translate", HttpMethod.PUT));
        Assert.assertEquals(EventAction.PATCH, definition.search("/translate", HttpMethod.PATCH));
        Assert.assertEquals(EventAction.REMOVE, definition.search("/translate", HttpMethod.DELETE));
    }

    @Test
    public void test_has_param_but_not_at_last() throws JSONException {
        EventMethodDefinition definition = EventMethodDefinition.create("/p/:pid/data", ActionMethodMapping.by(
            ActionMethodMapping.CRD_MAP,
            Arrays.asList(EventAction.GET_ONE, EventAction.CREATE_OR_UPDATE, EventAction.REMOVE)));
        Assert.assertTrue(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_ONE, definition.search("/p/123/data", HttpMethod.GET));
        Assert.assertEquals(EventAction.CREATE_OR_UPDATE, definition.search("/p/123/data", HttpMethod.PUT));
        Assert.assertEquals(EventAction.REMOVE, definition.search("/p/123/data", HttpMethod.DELETE));
        JSONAssert.assertEquals(
            "{\"servicePath\":\"/p/[^/]+/data\",\"mapping\":[{\"action\":\"GET_ONE\",\"method\":\"GET\"," +
            "\"capturePath\":\"/p/:pid/data\",\"regexPath\":\"/p/[^/]+/data\"},{\"action\":\"CREATE_OR_UPDATE\"," +
            "\"method\":\"PUT\",\"capturePath\":\"/p/:pid/data\",\"regexPath\":\"/p/[^/]+/data\"}," +
            "{\"action\":\"REMOVE\",\"method\":\"DELETE\",\"capturePath\":\"/p/:pid/data\"," +
            "\"regexPath\":\"/p/[^/]+/data\"}],\"useRequestData\":true}", definition.toJson().encode(),
            JSONCompareMode.LENIENT);
    }

    @Test
    public void test_search() {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/abc", "/:id");
        Assert.assertTrue(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_LIST, definition.search("/abc", HttpMethod.GET));
        Assert.assertEquals(EventAction.GET_ONE, definition.search("/abc/xyz", HttpMethod.GET));
        Assert.assertEquals(EventAction.CREATE, definition.search("/abc", HttpMethod.POST));
        Assert.assertEquals(EventAction.UPDATE, definition.search("/abc/xyz", HttpMethod.PUT));
        Assert.assertEquals(EventAction.PATCH, definition.search("/abc/xyz", HttpMethod.PATCH));
        Assert.assertEquals(EventAction.REMOVE, definition.search("/abc/xyz", HttpMethod.DELETE));
    }

    @Test
    public void test_search_multiParam_pattern_has_resource_between() {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/client/:clientId/product",
                                                                               "/:productId");
        Assert.assertTrue(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_LIST, definition.search("/client/123/product", HttpMethod.GET));
        Assert.assertEquals(EventAction.GET_ONE, definition.search("/client/123/product/456", HttpMethod.GET));
        Assert.assertEquals(EventAction.CREATE, definition.search("/client/123/product", HttpMethod.POST));
        Assert.assertEquals(EventAction.UPDATE, definition.search("/client/123/product/456", HttpMethod.PUT));
        Assert.assertEquals(EventAction.PATCH, definition.search("/client/123/product/456", HttpMethod.PATCH));
        Assert.assertEquals(EventAction.REMOVE, definition.search("/client/123/product/456", HttpMethod.DELETE));
    }

    @Test
    public void test_search_multiParam_pattern_no_resource_between() {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/client/:clientId/", "/:productId");
        Assert.assertTrue(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_LIST, definition.search("/client/123/", HttpMethod.GET));
        Assert.assertEquals(EventAction.GET_ONE, definition.search("/client/123/456", HttpMethod.GET));
        Assert.assertEquals(EventAction.CREATE, definition.search("/client/123/", HttpMethod.POST));
        Assert.assertEquals(EventAction.UPDATE, definition.search("/client/123/456", HttpMethod.PUT));
        Assert.assertEquals(EventAction.PATCH, definition.search("/client/123/456", HttpMethod.PATCH));
        Assert.assertEquals(EventAction.REMOVE, definition.search("/client/123/456", HttpMethod.DELETE));
    }

    @Test
    public void test_to_json() throws JSONException {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/abc", "/:id");
        System.out.println(definition.toJson());
        Assert.assertTrue(definition.isUseRequestData());
        JSONAssert.assertEquals("{\"servicePath\":\"/abc\",\"mapping\":[{\"action\":\"GET_LIST\",\"method\":\"GET\"}," +
                                "{\"action\":\"CREATE\",\"method\":\"POST\"},{\"action\":\"UPDATE\"," +
                                "\"method\":\"PUT\",\"capturePath\":\"/abc/:id\",\"regexPath\":\"/abc/.+\"}," +
                                "{\"action\":\"GET_ONE\",\"method\":\"GET\",\"capturePath\":\"/abc/:id\"," +
                                "\"regexPath\":\"/abc/.+\"},{\"action\":\"PATCH\",\"method\":\"PATCH\"," +
                                "\"capturePath\":\"/abc/:id\",\"regexPath\":\"/abc/.+\"},{\"action\":\"REMOVE\"," +
                                "\"method\":\"DELETE\",\"capturePath\":\"/abc/:id\",\"regexPath\":\"/abc/.+\"}]}\n",
                                definition.toJson().encode(), JSONCompareMode.LENIENT);
    }

    @Test
    public void test_to_json_multiParams() throws JSONException {
        EventMethodDefinition definition = EventMethodDefinition.createDefault("/c/:cId/p", "/:pId");
        System.out.println(definition.toJson());
        Assert.assertTrue(definition.isUseRequestData());
        JSONAssert.assertEquals("{\"servicePath\":\"/c/[^/]+/p\",\"mapping\":[{\"action\":\"GET_LIST\"," +
                                "\"method\":\"GET\",\"capturePath\":\"/c/:cId/p\",\"regexPath\":\"/c/[^/]+/p\"}," +
                                "{\"action\":\"CREATE\",\"method\":\"POST\",\"capturePath\":\"/c/:cId/p\"," +
                                "\"regexPath\":\"/c/[^/]+/p\"},{\"action\":\"UPDATE\",\"method\":\"PUT\"," +
                                "\"capturePath\":\"/c/:cId/p/:pId\",\"regexPath\":\"/c/[^/]+/p/.+\"}," +
                                "{\"action\":\"GET_ONE\",\"method\":\"GET\",\"capturePath\":\"/c/:cId/p/:pId\"," +
                                "\"regexPath\":\"/c/[^/]+/p/.+\"},{\"action\":\"PATCH\",\"method\":\"PATCH\"," +
                                "\"capturePath\":\"/c/:cId/p/:pId\",\"regexPath\":\"/c/[^/]+/p/.+\"}," +
                                "{\"action\":\"REMOVE\",\"method\":\"DELETE\",\"capturePath\":\"/c/:cId/p/:pId\"," +
                                "\"regexPath\":\"/c/[^/]+/p/.+\"}]}", definition.toJson().encode(),
                                JSONCompareMode.LENIENT);
    }

    @Test
    public void test_from_json() {
        EventMethodDefinition definition = JsonData.from(
            "{\"servicePath\":\"/abc\", \"mapping\":[{\"action\":\"GET_LIST\"," +
            "\"method\":\"GET\"},{\"action\":\"CREATE\",\"method\":\"POST\"},{\"action\":\"UPDATE\"," +
            "\"method\":\"PUT\",\"capturePath\":\"/abc/:id\"},{\"action\":\"GET_ONE\",\"method\":\"GET\"," +
            "\"capturePath\":\"/abc/:id\"},{\"action\":\"PATCH\",\"method\":\"PATCH\"," +
            "\"capturePath\":\"/abc/:id\"},{\"action\":\"REMOVE\",\"method\":\"DELETE\"," +
            "\"capturePath\":\"/abc/:id\"}]}", EventMethodDefinition.class);
        System.out.println(definition.toJson());
        Assert.assertTrue(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_LIST, definition.search("/abc", HttpMethod.GET));
        Assert.assertEquals(EventAction.GET_ONE, definition.search("/abc/xyz", HttpMethod.GET));
        Assert.assertEquals(EventAction.CREATE, definition.search("/abc", HttpMethod.POST));
        Assert.assertEquals(EventAction.UPDATE, definition.search("/abc/xyz", HttpMethod.PUT));
        Assert.assertEquals(EventAction.PATCH, definition.search("/abc/xyz", HttpMethod.PATCH));
        Assert.assertEquals(EventAction.REMOVE, definition.search("/abc/xyz", HttpMethod.DELETE));
    }

    @Test
    public void test_from_json_not_use_request_data() {
        EventMethodDefinition definition = JsonData.from(
            "{\"servicePath\":\"/abc\",\"useRequestData\":false, \"mapping\":[{\"action\":\"GET_LIST\"," +
            "\"method\":\"GET\"}]}", EventMethodDefinition.class);
        System.out.println(definition.toJson());
        Assert.assertFalse(definition.isUseRequestData());
        Assert.assertEquals(EventAction.GET_LIST, definition.search("/abc", HttpMethod.GET));
    }

    @Test
    public void test_search_similar_pattern() {
        EventMethodDefinition definition1 = EventMethodDefinition.createDefault("/device/:device_id/folder",
                                                                                "/:folder_id");
        EventMethodDefinition definition2 = EventMethodDefinition.createDefault(
            "/device/:device_id/folder/:folder_id/point", "/:point_id");
        Assert.assertEquals(EventAction.GET_ONE,
                            definition2.search("/device/123/folder/345/point/abc", HttpMethod.GET));
        Assert.assertEquals(EventAction.GET_LIST, definition2.search("/device/123/folder/345/point", HttpMethod.GET));
        Assert.assertEquals(EventAction.GET_ONE, definition1.search("/device/123/folder/345/point", HttpMethod.GET));
    }

}
