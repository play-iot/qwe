package io.zero88.qwe.micro.mock;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class MockEventBusServiceImpl implements MockEventBusService {

    @Override
    public MockEventBusService get(Handler<AsyncResult<JsonObject>> resultHandler) {
        resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "hello")));
        return this;
    }

    @Override
    public MockEventBusService post(String data, Handler<AsyncResult<JsonObject>> resultHandler) {
        resultHandler.handle(Future.succeededFuture(new JsonObject().put("request", data).put("data", "xxy")));
        return this;
    }

}
