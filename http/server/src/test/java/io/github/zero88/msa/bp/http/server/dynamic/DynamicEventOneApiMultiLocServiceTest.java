package io.github.zero88.msa.bp.http.server.dynamic;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zero88.msa.bp.http.server.dynamic.mock.MockEventOneApiMultiLocService;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import io.github.zero88.msa.bp.component.ContainerVerticle;

@RunWith(VertxUnitRunner.class)
public class DynamicEventOneApiMultiLocServiceTest extends DynamicServiceTestBase {

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends ContainerVerticle> T service() {
        return (T) new MockEventOneApiMultiLocService();
    }

    @Test
    public void test_get_list_success(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/p", 200, new JsonObject(
            "{\"data\":[{\"pId.01\":\"xxx\"},{\"pId.02\":\"abc\"},{\"pId.03\":\"123\"},{\"pId.04\":\"456\"}]}"));
    }

    @Test
    public void test_get_one_success(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/p/pId.01", 200, new JsonObject("{\"pId.01\":\"xxx\"}"));
    }

    @Test
    public void test_get_one_not_found(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/p/pId.05", 404,
                           new JsonObject("{\"message\":\"Not found\",\"code\":\"NOT_FOUND\"}"));
    }

    @Test
    public void test_get_list_by_another_return_empty(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/c/123/p", 200,
                           new JsonObject().put("data", new ArrayList<>()));
    }

    @Test
    public void test_get_list_by_another_return_success(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/c/cId.01/p", 200,
                           new JsonObject("{\"data\":[{\"pId.01\":\"xxx\"},{\"pId.02\":\"abc\"}]}"));
    }

    @Test
    public void test_get_one_by_another_success(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/c/cId.02/p/pId.04", 200,
                           new JsonObject("{\"pId.04\":\"456\"}"));
    }

    @Test
    public void test_get_one_by_another_not_found(TestContext context) {
        assertRestByClient(context, HttpMethod.GET, "/api/s/c/cId.02/p/pId.02", 404,
                           new JsonObject("{\"message\":\"Not found\",\"code\":\"NOT_FOUND\"}"));
    }

}
