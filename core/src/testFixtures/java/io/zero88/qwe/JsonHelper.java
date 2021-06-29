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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.qwe.dto.JsonData;

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

    static void assertJson(JsonObject expected, JsonObject actual) {
        assertJson(expected, actual, JSONCompareMode.STRICT);
    }

    static void assertJson(JsonObject expected, JsonObject actual, Customization... customizations)
        throws JSONException {
        try {
            JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
        } catch (JSONException | AssertionError e) {
            throw logError(expected, actual, e);
        }
    }

    static void assertJson(JsonObject expected, JsonObject actual, JSONCompareMode mode) {
        try {
            JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
        } catch (JSONException | AssertionError e) {
            throw logError(expected, actual, e);
        }
    }

    final class Junit4 {

        public static Consumer<Object> asserter(TestContext context, Async async, JsonObject expected) {
            return resp -> assertJson(context, async, expected, (JsonObject) resp, JSONCompareMode.STRICT);
        }

        public static Consumer<Object> asserter(TestContext context, Async async, JsonObject expected,
                                                JSONCompareMode mode) {
            return resp -> assertJson(context, async, expected, (JsonObject) resp, mode);
        }

        public static void assertJson(TestContext context, Async async, JsonObject expected, Buffer buffer) {
            assertJson(context, async, expected, JsonData.tryParse(buffer).toJson());
        }

        public static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                                      JSONCompareMode mode) {
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
                TestHelper.LOGGER.debug("Actual: " + actual.encode());
            } catch (JSONException | AssertionError e) {
                context.fail(logError(expected, actual, e));
            } finally {
                TestHelper.testComplete(async);
            }
        }

        public static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                                      List<Customization> customizations) {
            assertJson(context, async, expected, actual,
                       Optional.ofNullable(customizations).orElse(new ArrayList<>()).toArray(new Customization[] {}));
        }

        public static void assertJson(TestContext context, Async async, JsonObject expected, JsonObject actual,
                                      Customization... customizations) {
            if (customizations.length == 0) {
                assertJson(context, async, expected, actual, JSONCompareMode.STRICT);
                return;
            }
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
            } catch (JSONException | AssertionError e) {
                context.fail(logError(expected, actual, e));
            } finally {
                TestHelper.testComplete(async);
            }
        }

    }


    final class Junit5 {

        public static Consumer<Object> asserter(VertxTestContext context, Checkpoint flag, JsonObject expected) {
            return resp -> assertJson(context, flag, expected, (JsonObject) resp, JSONCompareMode.STRICT);
        }

        public static Consumer<Object> asserter(VertxTestContext context, Checkpoint flag, JsonObject expected,
                                                JSONCompareMode mode) {
            return resp -> assertJson(context, flag, expected, (JsonObject) resp, mode);
        }

        public static void assertJson(VertxTestContext context, Checkpoint flag, JsonObject expected, JsonObject actual,
                                      JSONCompareMode mode) {
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), mode);
                TestHelper.LOGGER.debug("Actual: " + actual.encode());
            } catch (JSONException | AssertionError e) {
                context.failNow(logError(expected, actual, e));
            } finally {
                flag.flag();
            }
        }

        public static void assertJson(VertxTestContext context, Checkpoint flag, JsonObject expected, JsonObject actual,
                                      List<Customization> customizations) {
            assertJson(context, flag, expected, actual,
                       Optional.ofNullable(customizations).orElse(new ArrayList<>()).toArray(new Customization[] {}));
        }

        public static void assertJson(VertxTestContext context, Checkpoint flag, JsonObject expected, JsonObject actual,
                                      Customization... customizations) {
            if (customizations.length == 0) {
                assertJson(context, flag, expected, actual, JSONCompareMode.STRICT);
                return;
            }
            try {
                JSONAssert.assertEquals(expected.encode(), actual.encode(), comparator(customizations));
            } catch (JSONException | AssertionError e) {
                context.failNow(logError(expected, actual, e));
            } finally {
                flag.flag();
            }
        }

    }

    static RuntimeException logError(JsonObject expected, JsonObject actual, Throwable e) {
        TestHelper.LOGGER.error("Actual: " + actual.encode());
        TestHelper.LOGGER.error("Expected: " + expected.encode());
        return new RuntimeException(e);
    }

}
