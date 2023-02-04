package cloud.playio.qwe.http.client;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import cloud.playio.qwe.JsonHelper.Junit5;
import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.exceptions.TimeoutException;
import cloud.playio.qwe.http.HttpException;

class HttpClientExtensionTest extends HttpExtensionTestBase {

    @Test
    void test_request_simple(VertxTestContext context) {
        Checkpoint cp = context.checkpoint();
        RequestOptions reqOpts = new RequestOptions().setHost("postman-echo.com")
                                                     .setURI("/get?foo1=bar1&foo2=bar2")
                                                     .setMethod(HttpMethod.GET);
        extension.entrypoint().request(reqOpts).onFailure(context::failNow).onSuccess(resp -> context.verify(() -> {
            Assertions.assertNotNull(resp);
            Assertions.assertNotNull(resp.body());
            Junit5.assertJson(context, cp, new JsonObject("{\"foo1\":\"bar1\",\"foo2\":\"bar2\"}"),
                              resp.body().getJsonObject("args"));
        }));
    }

    @Test
    public void test_failed_resolve_dns(VertxTestContext context) {
        Checkpoint cp = context.checkpoint();
        RequestOptions reqOpts = new RequestOptions().setHost("postman-echo1.xxx");
        extension.entrypoint().request(reqOpts).onFailure(t -> context.verify(() -> {
            Assertions.assertTrue(t instanceof HttpException);
            Assertions.assertTrue(t.getCause() instanceof UnknownHostException);
            cp.flag();
        })).onSuccess(r -> context.failNow("Failed test must not success"));
    }

    @Test
    public void test_connection_timeout(Vertx vertx, VertxTestContext context) {
        Checkpoint cp = context.checkpoint();
        HttpClientOptions options = new HttpClientOptions().setConnectTimeout(2000)
                                                           .setIdleTimeout(1)
                                                           .setIdleTimeoutUnit(TimeUnit.SECONDS);
        HttpClientExtension ext = initExtension(vertx, HttpClientExtension.class,
                                                new HttpClientConfig().setOptions(options));
        RequestOptions reqOpts = new RequestOptions().setHost("postman-echo.com")
                                                     .setURI("/delay/5")
                                                     .setMethod(HttpMethod.GET);
        ext.entrypoint().request(reqOpts).onFailure(t -> context.verify(() -> {
            Assertions.assertTrue(t instanceof TimeoutException);
            cp.flag();
        })).onSuccess(r -> context.failNow("Failed test must not success"));
    }

    @Test
    public void test_not_found_shallow_error(VertxTestContext context) {
        Checkpoint cp = context.checkpoint();
        RequestOptions reqOpts = new RequestOptions().setHost("postman-echo.com")
                                                     .setURI("/xxx")
                                                     .setMethod(HttpMethod.GET);
        extension.entrypoint()
                 .request(reqOpts, null, true)
                 .onFailure(context::failNow)
                 .onSuccess(resp -> context.verify(() -> {
                     Assertions.assertEquals(404, resp.getStatus().code());
                     cp.flag();
                 }));
    }

    @Test
    public void test_not_found_throw_error(VertxTestContext context) {
        Checkpoint cp = context.checkpoint();
        RequestOptions reqOpts = new RequestOptions().setHost("postman-echo.com")
                                                     .setURI("/xxx")
                                                     .setMethod(HttpMethod.GET);
        extension.entrypoint().request(reqOpts).onFailure(t -> context.verify(() -> {
            Assertions.assertTrue(t instanceof QWEException);
            Assertions.assertEquals(ErrorCode.DATA_NOT_FOUND, ((QWEException) t).errorCode());
            cp.flag();
        })).onSuccess(r -> context.failNow("Failed test must not success"));
    }

}
