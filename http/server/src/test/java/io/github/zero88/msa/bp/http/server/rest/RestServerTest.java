package io.github.zero88.msa.bp.http.server.rest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.msa.bp.TestHelper;
import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.github.zero88.msa.bp.exceptions.InitializerError;
import io.github.zero88.msa.bp.http.server.HttpServerRouter;
import io.github.zero88.msa.bp.http.server.HttpServerTestBase;
import io.github.zero88.msa.bp.http.server.mock.MockApiDefinition;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class RestServerTest extends HttpServerTestBase {

    @Rule
    public Timeout timeout = Timeout.seconds(TestHelper.TEST_TIMEOUT_SEC);

    @Test
    public void test_not_yet_register(TestContext context) {
        startServer(context, new HttpServerRouter(), t -> context.assertTrue(t instanceof InitializerError));
    }

    @Test
    public void test_none_api_not_found(TestContext context) {
        String path = "/abc/";
        JsonObject expected = notFoundResponse(httpConfig.getPort(), path);
        startServer(context, new HttpServerRouter().registerApi(MockApiDefinition.MockAPI.class));
        assertRestByClient(context, HttpMethod.GET, path, 404, expected);
    }

    @Test
    public void test_api_not_found(TestContext context) {
        String path = "/api/xx";
        JsonObject expected = notFoundResponse(httpConfig.getPort(), path);
        startServer(context, new HttpServerRouter().registerApi(MockApiDefinition.MockAPI.class));
        assertRestByClient(context, HttpMethod.GET, path, 404, expected);
    }

    @Test
    public void test_api_throwable(TestContext context) {
        String path = "/api/test/error";
        JsonObject expected = ErrorMessage.parse(ErrorCode.UNKNOWN_ERROR, "error").toJson();
        startServer(context, new HttpServerRouter().registerApi(MockApiDefinition.MockAPI.class));
        assertRestByClient(context, HttpMethod.GET, path, 500, expected);
    }

    @Test
    public void test_api_get_success(TestContext context) {
        String path = "/api/test";
        JsonObject expected = new JsonObject().put("abc", "xxx");
        startServer(context, new HttpServerRouter().registerApi(MockApiDefinition.MockAPI.class));
        assertRestByClient(context, HttpMethod.GET, path, 200, expected);
    }

}