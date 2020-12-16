package io.github.zero88.msa.bp.http.server.rest;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.msa.bp.exceptions.EngineException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.http.server.HttpServerRouter;
import io.github.zero88.msa.bp.http.server.HttpServerTestBase;
import io.github.zero88.msa.bp.http.server.mock.MockApiDefinition;
import io.github.zero88.msa.bp.http.server.mock.MockEventBusErrorListener;
import io.github.zero88.msa.bp.http.server.mock.MockEventBusSuccessListener;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class RestEventServerTest extends HttpServerTestBase {

    @Rule
    public Timeout timeout = Timeout.seconds(60);

    @Test
    public void test_api_eventbus_not_found(TestContext context) {
        String path = "/api/test/event";
        JsonObject expected = notFoundResponse(httpConfig.getPort(), path);
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.GET, path, 404, expected);
    }

    @Test
    public void test_api_eventbus_error_unexpected(TestContext context) {
        MockEventBusErrorListener.create(this.vertx.eventBus()).start();
        String path = "/api/test/events";
        //TODO need to check error message: Duplicate `cause`??
        JsonObject expected = new JsonObject().put("code", ErrorCode.UNKNOWN_ERROR.code())
                                              .put("message", "UNKNOWN_ERROR | Cause: xxx | Cause: xxx");
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.GET, path, 500, expected);
    }

    @Test
    public void test_api_eventbus_error_from_server(TestContext context) {
        MockEventBusErrorListener.create(this.vertx.eventBus()).start();
        String path = "/api/test/events";
        JsonObject expected = new JsonObject().put("code", EngineException.CODE.code()).put("message", "Engine error");
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.POST, path, 500, expected);
    }

    @Test
    public void test_api_eventbus_error_from_user(TestContext context) {
        MockEventBusErrorListener.create(this.vertx.eventBus()).start();
        String path = "/api/test/events/:event_id";
        JsonObject expected = new JsonObject().put("code", ErrorCode.INVALID_ARGUMENT.code()).put("message", "invalid");
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.PUT, path, 400, expected);
    }

    @Test
    public void test_api_eventbus_no_reply(TestContext context) {
        String path = "/api/test/events/:event_id";
        JsonObject expected = new JsonObject().put("code", ErrorCode.SERVICE_ERROR.code())
                                              .put("message", "Service unavailable");
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.GET, path, 503, expected);
    }

    @Test
    public void test_api_eventbus_success_data_list(TestContext context) {
        try {
            MockEventBusSuccessListener.create(this.vertx.eventBus()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "/api/test/events";
        JsonObject expected = new JsonObject().put("data", Arrays.asList("1", "2", "3"));
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.GET, path, 200, expected);
    }

    @Test
    public void test_api_eventbus_success_data_other(TestContext context) {
        try {
            MockEventBusSuccessListener.create(this.vertx.eventBus()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "/api/test/events/1";
        JsonObject expected = new JsonObject().put("data", 1);
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.GET, path, 200, expected);
    }

    @Test
    public void test_api_eventbus_success_data_json(TestContext context) {
        try {
            MockEventBusSuccessListener.create(this.vertx.eventBus()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "/api/test/events";
        JsonObject expected = new JsonObject().put("create", "success");
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.POST, path, 201, expected);
    }

    @Test
    public void test_api_eventbus_success_data_single(TestContext context) {
        try {
            MockEventBusSuccessListener.create(this.vertx.eventBus()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "/api/test/events/1";
        JsonObject expected = new JsonObject().put("data", "success");
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.PUT, path, 200, expected);
    }

    @Test
    public void test_api_eventbus_success_data_single_json(TestContext context) {
        try {
            MockEventBusSuccessListener.create(this.vertx.eventBus()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = "/api/test/events/1";
        JsonObject expected = new JsonObject().put("patch", "success").put("event_id", 1);
        startServer(context, new HttpServerRouter().registerEventBusApi(MockApiDefinition.MockRestEventApi.class));
        assertRestByClient(context, HttpMethod.PATCH, path, 200, expected);
    }

}
