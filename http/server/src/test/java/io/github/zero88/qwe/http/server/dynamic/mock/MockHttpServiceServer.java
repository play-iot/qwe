package io.github.zero88.qwe.http.server.dynamic.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.github.zero88.qwe.component.ApplicationVerticle;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.http.HttpUtils;
import io.github.zero88.qwe.http.server.HttpServerContext;
import io.github.zero88.qwe.http.server.HttpServerProvider;
import io.github.zero88.qwe.http.server.HttpServerRouter;
import io.github.zero88.qwe.http.server.ServerInfo;
import io.github.zero88.qwe.http.server.rest.RestApi;
import io.github.zero88.qwe.micro.MicroContext;
import io.github.zero88.qwe.micro.MicroserviceProvider;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.HttpLocation;

public class MockHttpServiceServer extends ApplicationVerticle {

    private HttpServerContext httpContext;
    private MicroContext microContext;

    @Override
    public void start() {
        super.start();
        final HttpServerRouter httpRouter = new HttpServerRouter().registerApi(MockAPI.class);
        this.addProvider(new HttpServerProvider(httpRouter), c -> this.httpContext = (HttpServerContext) c)
            .addProvider(new MicroserviceProvider(), c -> this.microContext = (MicroContext) c);
        this.registerSuccessHandler(event -> {
            ServerInfo info = this.httpContext.getServerInfo();
            microContext.getLocalController()
                        .addHttpRecord("httpService", new HttpLocation(info.toJson()).setRoot(info.getApiPath()),
                                       new JsonObject())
                        .subscribe();
        });
    }

    public String configFile() { return "httpService.json"; }

    @Path("/test")
    public static class MockAPI implements RestApi {

        @GET
        @Produces(HttpUtils.JSON_UTF8_CONTENT_TYPE)
        public JsonObject get() {
            return new JsonObject().put("hello", "dynamic");
        }

        @GET
        @Path("/error")
        @Produces(HttpUtils.JSON_UTF8_CONTENT_TYPE)
        public JsonObject error() {
            throw new CarlException("error");
        }

    }

}
