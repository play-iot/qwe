package io.github.zero88.msa.bp.http.server.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.http.HttpUtils;
import io.github.zero88.msa.bp.http.server.rest.AbstractRestEventApi;
import io.github.zero88.msa.bp.http.server.rest.RestApi;
import io.vertx.core.json.JsonObject;

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
            throw new BlueprintException("error");
        }

    }


    public static class MockRestEventApi extends AbstractRestEventApi {

        @Override
        public MockRestEventApi initRouter() {
            EventModel model = EventModel.builder()
                                         .address("http.server.test")
                                         .addEvents(EventAction.GET_LIST, EventAction.GET_ONE, EventAction.CREATE,
                                                    EventAction.UPDATE, EventAction.PATCH)
                                         .build();
            this.addRouter(model, "/test/events", "/:event_id");
            return this;
        }

    }

}
