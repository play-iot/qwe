package io.github.zero88.msa.blueprint.dto;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import io.github.zero88.msa.blueprint.TestHelper.JsonHelper;
import io.github.zero88.msa.blueprint.dto.jpa.Sort;
import io.vertx.core.json.JsonObject;

public class SortTest {

    @Test
    public void test_encode_decode() throws JSONException {
        final Sort sort = Sort.from("abc,-def,+xyz,g1.abc,g2.def,-g2.xy");
        Assert.assertNotNull(sort);
        final JsonObject expected = sort.toJson();
        System.out.println(expected);
        final Sort convert = JsonData.convert(expected, Sort.class);
        JsonHelper.assertJson(expected, convert.toJson());
    }

    @Test
    public void test_alternative_decode() throws JSONException {
        final Sort sort = Sort.from("abc,-def,+xyz,g1.abc,g2.def,-g2.xy");
        Assert.assertNotNull(sort);
        System.out.println(sort.toJson());
        final Sort convert = JsonData.convert(new JsonObject(
            "{\"g1.abc\":\"\",\"abc\":null,\"def\":\"-\",\"g2.xy\":\"-\",\"g2.def\":\"+\",\"xyz\":\"+\"}"), Sort.class);
        JsonHelper.assertJson(sort.toJson(), convert.toJson());
    }

}
