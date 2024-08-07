package cloud.playio.qwe.http.server.rest;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.exceptions.ErrorCode;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.dto.ErrorMessage;
import cloud.playio.qwe.http.server.HttpServerRouter;
import cloud.playio.qwe.http.server.HttpServerPluginTestBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import cloud.playio.qwe.http.server.RestApiTestHelper;
import cloud.playio.qwe.http.server.mock.MockRestAPI;

@RunWith(VertxUnitRunner.class)
public class RestApiTest extends HttpServerPluginTestBase implements RestApiTestHelper {

    @Rule
    public Timeout timeout = Timeout.seconds(TestHelper.TEST_TIMEOUT_SEC);

    @Test
    public void test_none_api_not_found(TestContext context) {
        String path = "/abc/";
        JsonObject expected = RestApiTestHelper.notFoundResponse(httpConfig.getPort(), path);
        startServer(context, new HttpServerRouter().registerApi(MockRestAPI.class));
        sendToApiThenAssert(context, HttpMethod.GET, path, 404, expected);
    }

    @Test
    public void test_api_not_found(TestContext context) {
        String path = "/api/xx";
        JsonObject expected = RestApiTestHelper.notFoundResponse(httpConfig.getPort(), path);
        startServer(context, new HttpServerRouter().registerApi(MockRestAPI.class));
        sendToApiThenAssert(context, HttpMethod.GET, path, 404, expected);
    }

    @Test
    @Ignore
    //TODO FIX `javax.ws.rs`
    public void test_api_throwable(TestContext context) {
        String path = "/api/test/error";
        JsonObject expected = ErrorMessage.parse(ErrorCode.UNKNOWN_ERROR, "error").toJson();
        startServer(context, new HttpServerRouter().registerApi(MockRestAPI.class));
        sendToApiThenAssert(context, HttpMethod.GET, path, 500, expected);
    }

    @Test
    @Ignore
    //TODO FIX `javax.ws.rs`
    public void test_api_get_success(TestContext context) {
        String path = "/api/test";
        JsonObject expected = new JsonObject().put("abc", "xxx");
        startServer(context, new HttpServerRouter().registerApi(MockRestAPI.class));
        sendToApiThenAssert(context, HttpMethod.GET, path, 200, expected);
    }

}
