package io.github.zero88.msa.blueprint.component;

import io.github.zero88.msa.blueprint.TestHelper.JsonHelper;
import io.github.zero88.msa.blueprint.component.ApplicationProbeHandler.ApplicationReadinessHandler;
import io.github.zero88.msa.blueprint.dto.ErrorData;
import io.github.zero88.msa.blueprint.dto.msg.RequestData;
import io.github.zero88.msa.blueprint.event.EventContractor;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

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
