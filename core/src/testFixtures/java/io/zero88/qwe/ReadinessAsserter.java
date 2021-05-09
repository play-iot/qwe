package io.zero88.qwe;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.zero88.qwe.ApplicationProbeHandler.ApplicationReadinessHandler;
import io.zero88.qwe.dto.ErrorData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventContractor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ReadinessAsserter implements ApplicationReadinessHandler {

    @NonNull
    private final TestContext context;
    @NonNull
    private final Async async;
    @NonNull
    private final JsonObject expected;

    @Override
    @EventContractor(action = "NOTIFY", returnType = boolean.class)
    public boolean success(RequestData requestData) {
        JsonHelper.assertJson(context, async, expected, requestData.body());
        return true;
    }

    @Override
    @EventContractor(action = "NOTIFY_ERROR", returnType = boolean.class)
    public boolean error(ErrorData error) {
        JsonHelper.assertJson(context, async, expected, error.toJson());
        return true;
    }

}
