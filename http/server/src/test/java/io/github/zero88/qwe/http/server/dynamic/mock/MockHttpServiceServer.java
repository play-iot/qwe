package io.github.zero88.qwe.http.server.dynamic.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.github.zero88.qwe.component.ApplicationVerticle;
import io.github.zero88.qwe.component.ContextLookup;
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

    @Override
    public void start() {
        super.start();
        this.addProvider(new HttpServerProvider(new HttpServerRouter().registerApi(MockAPI.class)))
            .addProvider(new MicroserviceProvider());
    }

    public String configFile() { return "httpService.json"; }

    @Override
    public void onInstallCompleted(ContextLookup lookup) {
        final HttpServerContext httpContext = lookup.query(HttpServerContext.class);
        final MicroContext microContext = lookup.query(MicroContext.class);
        final ServerInfo info = httpContext.getServerInfo();
        microContext.getLocalController()
                    .addHttpRecord("httpService", new HttpLocation(info.toJson()).setRoot(info.getApiPath()),
                                   new JsonObject())
                    .subscribe();
    }

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
