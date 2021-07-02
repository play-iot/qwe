package io.zero88.qwe.http.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.zero88.qwe.JsonHelper.Junit4;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.http.HttpUtils;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

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
    private Consumer<ResponseData> after;

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
        System.out.println("Response asserting...");
        System.out.println(actual.getStatus());
        try {
            context.assertEquals(HttpUtils.JSON_UTF8_CONTENT_TYPE,
                                 actual.headers().getString(HttpHeaders.CONTENT_TYPE.toString()));
            context.assertNotNull(actual.headers().getString("x-response-time"));
            context.assertEquals(code, actual.getStatus().code());
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

    public static class Builder {

        public Builder customizations(Customization... customizations) {
            this.customizations = Arrays.asList(customizations);
            return this;
        }

    }

}
