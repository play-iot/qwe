package cloud.playio.qwe.http.server;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.unit.Async;
import cloud.playio.qwe.BaseExtensionTest;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.http.client.HttpClientWrapper;

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
