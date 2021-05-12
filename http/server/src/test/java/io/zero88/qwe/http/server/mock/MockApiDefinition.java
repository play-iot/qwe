package io.zero88.qwe.http.server.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.vertx.core.json.JsonObject;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.rest.api.AbstractRestEventApi;
import io.zero88.qwe.http.server.rest.api.RestApi;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MockApiDefinition {

    @Path("/test")
    public static class MockAPI implements RestApi {

        @GET
        @Produces(HttpUtils.JSON_UTF8_CONTENT_TYPE)
        public JsonObject get() {
            return new JsonObject().put("abc", "xxx");
        }

        @GET
        @Path("/error")
        @Produces(HttpUtils.JSON_UTF8_CONTENT_TYPE)
        public JsonObject error() {
            throw new QWEException("error");
        }

    }


    public static class MockRestEventApi extends AbstractRestEventApi {

        @Override
        public MockRestEventApi initRouter() {
            this.addRouter("http.server.test", "/test/events", "/:event_id");
            return this;
        }

    }

}
