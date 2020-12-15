package io.github.zero88.msa.bp.dto;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.jpa.Sortable.Order;
import io.github.zero88.msa.bp.TestHelper.JsonHelper;
import io.github.zero88.msa.bp.dto.jpa.Pagination;
import io.github.zero88.msa.bp.dto.jpa.Sort;
import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.vertx.core.json.JsonObject;

public class RequestDataTest {

    @Test
    public void test_to_json() {
        RequestData requestData = RequestData.builder()
                                             .pagination(Pagination.builder().build())
                                             .body(new JsonObject().put("name", "hello"))
                                             .filter(new JsonObject().put("x", "test"))
                                             .build();
        Assertions.assertEquals("hello", requestData.body().getString("name"));
        Assertions.assertEquals("test", requestData.filter().getString("x"));
        Assertions.assertEquals(1, requestData.pagination().getPage());
        Assertions.assertEquals(20, requestData.pagination().getPerPage());
        Assertions.assertEquals("{\"headers\":{},\"body\":{\"name\":\"hello\"},\"filter\":{\"x\":\"test\"}," +
                                "\"pagination\":{\"_page\":1,\"_per_page\":20}}", requestData.toJson().encode());
    }

    @Test
    public void test_to_json_without_pagination() {
        RequestData requestData = RequestData.builder()
                                             .body(new JsonObject().put("name", "hello"))
                                             .filter(new JsonObject().put("x", "test"))
                                             .build();
        Assertions.assertEquals("hello", requestData.body().getString("name"));
        Assertions.assertEquals("test", requestData.filter().getString("x"));
        Assertions.assertNull(requestData.pagination());
        Assertions.assertEquals("{\"headers\":{},\"body\":{\"name\":\"hello\"},\"filter\":{\"x\":\"test\"}}",
                                requestData.toJson().encode());
    }

    @Test
    public void test_from_json_1() {
        final JsonObject pagination = new JsonObject().put("_page", 5).put("_per_page", 10);
        final JsonObject data = new JsonObject().put("pagination", pagination)
                                                .put("body", new JsonObject())
                                                .put("filter", new JsonObject());
        final RequestData requestData = data.mapTo(RequestData.class);
        Assertions.assertTrue(requestData.body().isEmpty());
        Assertions.assertTrue(requestData.filter().isEmpty());
        Assertions.assertNotNull(requestData.pagination());
        Assertions.assertEquals(5, requestData.pagination().getPage());
        Assertions.assertEquals(10, requestData.pagination().getPerPage());
    }

    @Test
    public void test_from_json_2() {
        final JsonObject pagination = new JsonObject().put("_page", 5).put("_per_page", 10);
        final JsonObject data = new JsonObject().put("pagination", pagination)
                                                .put("body", new JsonObject().put("name", "xyz"))
                                                .put("filter", new JsonObject().put("key", "1"));
        RequestData requestData = JsonData.from(data, RequestData.class);
        Assertions.assertEquals("xyz", requestData.body().getString("name"));
    }

    @Test
    public void test_encode_decode() throws JSONException {
        final JsonObject expected = new JsonObject("{\"headers\":{\"h1\":\"v1\"},\"body\":{\"b1\":\"v2\"}," +
                                                   "\"filter\":{\"f1\":\"v3\"},\"sort\":{\"s1\":\"ASC\"," +
                                                   "\"s2\":\"DESC\"}}");
        RequestData requestData = RequestData.builder()
                                             .headers(new JsonObject().put("h1", "v1"))
                                             .body(new JsonObject().put("b1", "v2"))
                                             .filter(new JsonObject().put("f1", "v3"))
                                             .sort(Sort.builder()
                                                       .item(Order.byASC("s1"))
                                                       .item(Order.byDESC("s2"))
                                                       .build())
                                             .build();
        JsonHelper.assertJson(expected, requestData.toJson());
        final RequestData from = JsonData.from(expected, RequestData.class);
        JsonHelper.assertJson(expected, from.toJson());
    }

}
