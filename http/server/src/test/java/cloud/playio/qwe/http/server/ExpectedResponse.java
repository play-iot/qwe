package cloud.playio.qwe.http.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.github.zero88.utils.Strings;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import cloud.playio.qwe.JsonHelper.Junit4;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.http.HttpUtils;

import lombok.Builder;
import lombok.NonNull;

@Builder(builderClassName = "Builder")
public class ExpectedResponse {

    @NonNull
    private final JsonObject expected;
    private final int code;
    @NonNull
    private final List<Customization> customizations;
    @NonNull
    private final JSONCompareMode mode;
    private final Consumer<ResponseData> after;

    ExpectedResponse(JsonObject expected, int code, List<Customization> customizations, JSONCompareMode mode,
                     Consumer<ResponseData> after) {
        this.expected = expected;
        this.code = code;
        this.after = after;
        this.mode = Optional.ofNullable(mode).orElse(JSONCompareMode.STRICT);
        this.customizations = new ArrayList<>(Optional.ofNullable(customizations).orElseGet(ArrayList::new));
    }

    public boolean hasAfter() {
        return Objects.nonNull(after);
    }

    public void _assert(@NonNull TestContext context, Async async, @NonNull ResponseData actual) {
        try {
            assertHeaders(context, actual);
            if (customizations.isEmpty()) {
                Junit4.assertJson(context, async, expected, actual.body(), mode);
            } else {
                Junit4.assertJson(context, async, expected, actual.body(), customizations);
            }
            Optional.ofNullable(after).ifPresent(c -> c.accept(actual));
        } finally {
            TestHelper.testComplete(async);
        }
    }

    private void assertHeaders(@NonNull TestContext context, ResponseData actual) {
        try {
            context.assertEquals(HttpUtils.JSON_UTF8_CONTENT_TYPE,
                                 actual.headers().getString(HttpHeaders.CONTENT_TYPE.toString()));
            context.assertNotNull(actual.headers().getString("x-response-time"));
            context.assertEquals(code, actual.getStatus().code());
        } catch (AssertionError err) {
            TestHelper.LOGGER.error("[{}] {}", Strings.padLeft("BODY", 8), actual.body());
            throw err;
        }
    }

    public static class Builder {

        public Builder customizations(Customization... customizations) {
            this.customizations = Arrays.asList(customizations);
            return this;
        }

    }

}
