package io.zero88.qwe.http.server;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.unit.Async;
import io.zero88.qwe.BaseExtensionTest;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.http.client.HttpClientWrapper;

public interface HttpServerTestHelper extends BaseExtensionTest {

    Vertx vertx();

    HttpClientWrapper client();

    RequestOptions requestOptions();

    default Handler<Void> closeClient() {
        return e -> client().unwrap().close();
    }

    default void testComplete(Async async) {
        testComplete(async, "");
    }

    default void testComplete(Async async, String msgEvent) {
        TestHelper.testComplete(async, msgEvent, closeClient());
    }

}
