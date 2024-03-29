package cloud.playio.qwe.http.server.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.vertx.core.json.JsonObject;
import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.http.HttpUtils;
import cloud.playio.qwe.http.server.rest.api.RestApi;

@Path("/test")
public class MockRestAPI implements RestApi {

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
