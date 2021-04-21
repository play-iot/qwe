package io.zero88.qwe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.json.JSONException;
import org.skyscreamer.jsonassert.ArrayValueMatcher;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import io.zero88.qwe.dto.JsonData;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

import lombok.NonNull;

public interface JsonHelper {

    static Customization ignore(@NonNull String path) {
        return new Customization(path, (o1, o2) -> true);
    }

    static Customization ignoreInArray(@NonNull String path, @NonNull String arrayPath) {
        ArrayValueMatcher<Object> arrValMatch = new ArrayValueMatcher<>(
            new CustomComparator(JSONCompareMode.NON_EXTENSIBLE, new Customization(path, (o1, o2) -> true)));
        return new Customization(arrayPath, arrValMatch);
    }

    static CustomComparator comparator(Customization... customizations) {
        return new CustomComparator(JSONCompareMode.LENIENT, customizations);
    }

    static Consumer<Object> asserter(TestContext context, Async async, JsonObject expected) {
        return resp -> JsonHelper.assertJson(context, async, expected, (JsonObject) resp, JSONCompareMode.STRICT);
    }

    static Consumer<Object> asserter(TestContext context, Async async, JsonObject expected, JSONCompareMode mode) {
        return resp -> JsonHelper.assertJson(context, async, expected, (JsonObject) resp, mode);
    }

    static void assertJson(JsonObject expected, JsonObject actual) throws JSONException {
        assertJson(expected, actual, JSONCompareMode.STRICT);
    }

    static void assertJson(JsonObject expected, JsonObject actual, Customization... customizations)
        throws JSONException {
        try {
            JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
        } catch (JSONException | AssertionError e) {
            System.out.println("Actual: " + actual.encode());
            System.out.println("Expected: " + expected.encode());
            throw e;
        }
    }

    static void assertJson(JsonObject expected, JsonObject actual, JSONCompareMode mode) throws JSONException {
        try {
            JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
        } catch (JSONException | AssertionError e) {
            System.out.println("Actual: " + actual.encode());
            System.out.println("Expected: " + expected.encode());
            throw e;
        }
    }

    static void assertJson(TestContext context, Async async, JsonObject expected, Buffer buffer) {
        assertJson(context, async, expected, JsonData.tryParse(buffer).toJson());
    }

    static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                           JSONCompareMode mode) {
        try {
            JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
            System.out.println("Actual: " + actual.encode());
        } catch (JSONException | AssertionError e) {
            System.out.println("Actual: " + actual.encode());
            System.out.println("Expected: " + expected.encode());
            context.fail(e);
        } finally {
            TestHelper.testComplete(async);
        }
    }

    static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                           List<Customization> customizations) {
        assertJson(context, async, expected, actual,
                   Optional.ofNullable(customizations).orElse(new ArrayList<>()).toArray(new Customization[] {}));
    }

    static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                           Customization... customizations) {
        if (customizations.length == 0) {
            assertJson(context, async, expected, actual, JSONCompareMode.STRICT);
            return;
        }
        try {
            JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
        } catch (JSONException | AssertionError e) {
            System.out.println("Actual: " + actual.encode());
            System.out.println("Expected: " + expected.encode());
            context.fail(e);
        } finally {
            TestHelper.testComplete(async);
        }
    }

}
