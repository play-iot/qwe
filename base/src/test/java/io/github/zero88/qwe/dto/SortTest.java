package io.github.zero88.qwe.dto;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.qwe.TestHelper.JsonHelper;
import io.github.zero88.qwe.dto.jpa.Sort;
import io.vertx.core.json.JsonObject;

public class SortTest {

    @Test
    public void test_encode_decode() throws JSONException {
        final Sort sort = Sort.from("abc,-def,+xyz,g1.abc,g2.def,-g2.xy");
        Assertions.assertNotNull(sort);
        final JsonObject expected = sort.toJson();
        System.out.println(expected);
        final Sort convert = JsonData.convert(expected, Sort.class);
        JsonHelper.assertJson(expected, convert.toJson());
    }

    @Test
    public void test_alternative_decode() throws JSONException {
        final Sort sort = Sort.from("abc,-def,+xyz,g1.abc,g2.def,-g2.xy");
        Assertions.assertNotNull(sort);
        System.out.println(sort.toJson());
        final Sort convert = JsonData.convert(new JsonObject(
            "{\"g1.abc\":\"\",\"abc\":null,\"def\":\"-\",\"g2.xy\":\"-\",\"g2.def\":\"+\",\"xyz\":\"+\"}"), Sort.class);
        JsonHelper.assertJson(sort.toJson(), convert.toJson());
    }

}
