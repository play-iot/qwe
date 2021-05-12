package io.zero88.qwe.http.server.dynamic.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.ApplicationVerticle;
import io.zero88.qwe.ContextLookup;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.HttpServerContext;
import io.zero88.qwe.http.server.HttpServerProvider;
import io.zero88.qwe.http.server.HttpServerRouter;
import io.zero88.qwe.http.server.ServerInfo;
import io.zero88.qwe.http.server.rest.api.RestApi;
import io.zero88.qwe.micro.MicroContext;
import io.zero88.qwe.micro.MicroVerticleProvider;

public class MockHttpServiceServer extends ApplicationVerticle {

    @Override
    public void start() {
        super.start();
        this.addProvider(new HttpServerProvider(new HttpServerRouter().registerApi(MockAPI.class)))
            .addProvider(new MicroVerticleProvider());
    }

    public String configFile() { return "httpService.json"; }

    @Override
    public void onInstallCompleted(ContextLookup lookup) {
        final HttpServerContext httpContext = lookup.query(HttpServerContext.class);
        final MicroContext microContext = lookup.query(MicroContext.class);
        final ServerInfo info = httpContext.getServerInfo();
        microContext.getLocalInvoker()
                    .addHttpRecord("httpService", new HttpLocation(info.toJson()).setRoot(info.getApiPath()),
                                   new JsonObject())
                    .mapEmpty();
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
