package io.github.zero88.qwe.micro.mock;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface MockEventbusService {

    String SERVICE_ADDRESS = "qwe.micro.mock.MockEventbusService";

    String SERVICE_NAME = "mock-eventbus";

    @Fluent
    MockEventbusService get(Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    MockEventbusService post(String data, Handler<AsyncResult<JsonObject>> resultHandler);

}
