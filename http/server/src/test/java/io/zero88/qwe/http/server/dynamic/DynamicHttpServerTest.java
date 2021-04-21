package io.zero88.qwe.http.server.dynamic;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.zero88.qwe.TestHelper;
import io.zero88.qwe.JsonHelper;
import io.zero88.qwe.component.ApplicationVerticle;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.http.server.dynamic.mock.MockHttpServiceServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class DynamicHttpServerTest extends DynamicServiceTestBase {

    private int port;

    protected DeploymentOptions getServiceOptions() throws IOException {
        port = TestHelper.getRandomPort();
        return new DeploymentOptions().setConfig(deployConfig(port));
    }

    @Override
    protected <T extends ApplicationVerticle> T service() {
        return (T) new MockHttpServiceServer();
    }

    @Test
    @Ignore
    //TODO FIX `javax.ws.rs`
    public void test_get_success(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/rest/test", 200, new JsonObject().put("hello", "dynamic"));
    }

    @Test
    @Ignore
    //TODO: Review `message`: is plain text, not json object
    public void test_error(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/rest/test/error", 500,
                           new JsonObject().put("code", ErrorCode.UNKNOWN_ERROR.code())
                                           .put("message", new JsonObject().put("code", ErrorCode.UNKNOWN_ERROR.code())
                                                                           .put("message", "error")));
    }

    @Test
    @Ignore
    //TODO: Review `message`: is plain text, not json object
    public void test_not_found(TestContext context) {
        JsonObject m = new JsonObject().put("message", "Resource not found");
        assertRestByClient(context, HttpMethod.GET, "/api/s/rest/xxx", 404,
                           new JsonObject().put("code", ErrorCode.NOT_FOUND.code()).put("message", m),
                           JsonHelper.ignore("message.uri"));
    }

    @Test
    public void test_get_gateway_index(TestContext context) {
        final JsonObject expected = new JsonObject(
            "{\"apis\":[{\"name\":\"httpService\",\"type\":\"http-endpoint\",\"status\":\"UP\"," +
            "\"location\":\"http://0.0.0.0:" + port + "/rest\"}]}");
        assertRestByClient(context, HttpMethod.GET, "/gw/index", 200, expected);
    }

}
