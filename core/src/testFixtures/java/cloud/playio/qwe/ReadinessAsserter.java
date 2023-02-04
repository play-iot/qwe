package cloud.playio.qwe;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import cloud.playio.qwe.ApplicationProbeHandler.ApplicationReadinessHandler;
import cloud.playio.qwe.JsonHelper.Junit4;
import cloud.playio.qwe.dto.ErrorData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;

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
    @EBContract(action = "NOTIFY")
    public boolean success(RequestData requestData) {
        Junit4.assertJson(context, async, expected, requestData.body());
        return true;
    }

    @Override
    @EBContract(action = "NOTIFY_ERROR")
    public boolean error(ErrorData error) {
        Junit4.assertJson(context, async, expected, error.toJson());
        return true;
    }

}
