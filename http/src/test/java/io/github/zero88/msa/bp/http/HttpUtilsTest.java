package io.github.zero88.msa.bp.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.msa.bp.http.HttpUtils.HttpHeaderUtils;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

public class HttpUtilsTest {

    @Test
    public void test_serialize_headers() throws JSONException {
        final List<String> listValue = Arrays.asList("a", "b", "c");
        final MultiMap multiMap = MultiMap.caseInsensitiveMultiMap().add("key1", "2").add("key2", listValue);
        JSONAssert.assertEquals("{\"key1\":\"2\",\"key2\":[\"a\",\"b\",\"c\"]}",
                                HttpHeaderUtils.serializeHeaders(multiMap).toString(), JSONCompareMode.LENIENT);
    }

    @Test
    public void test_deserialize_headers() {
        JsonObject header = new JsonObject("{\"key1\":\"2\",\"key3\":3, \"key4\":true}");
        header.put("key5", Arrays.asList("a", "b", "c"));
        header.put("key6", new ArrayList<>(new HashSet<>(Arrays.asList("x", "y", "z", "x"))));
        final MultiMap entries = HttpHeaderUtils.deserializeHeaders(header);
        Assertions.assertFalse(entries.contains("key2"));
        Assertions.assertEquals("2", entries.get("key1"));
        Assertions.assertEquals("3", entries.get("key3"));
        Assertions.assertEquals("true", entries.get("key4"));
        Assertions.assertEquals("a", entries.get("key5"));
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), entries.getAll("key5"));
        Assertions.assertEquals(Arrays.asList("x", "y", "z"), entries.getAll("key6"));
    }

    @Test
    public void test_deserialize_uri() {
        QueryStringDecoder decoder = new QueryStringDecoder(
            "http://localhost?_q=node==abc,(node2=in=(123,456) and node3!=zzz) and " +
            "node4=gt=10&_page=1&_per_page=20&_audit&_pretty");
        Assertions.assertEquals(
            "_q=node==abc,(node2=in=(123,456) and node3!=zzz) and node4=gt=10&_page=1&_per_page=20&_audit&_pretty",
            decoder.rawQuery());
        Assertions.assertEquals("[node==abc,(node2=in=(123,456) and node3!=zzz) and node4=gt=10]",
                                decoder.parameters().get("_q").toString());
        System.out.println(decoder.parameters());
    }

}
