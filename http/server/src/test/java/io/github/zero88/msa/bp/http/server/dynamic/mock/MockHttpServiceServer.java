package io.github.zero88.msa.bp.http.server.dynamic.mock;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.github.zero88.msa.bp.component.ContainerVerticle;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.http.HttpUtils;
import io.github.zero88.msa.bp.http.server.HttpServerContext;
import io.github.zero88.msa.bp.http.server.HttpServerProvider;
import io.github.zero88.msa.bp.http.server.HttpServerRouter;
import io.github.zero88.msa.bp.http.server.ServerInfo;
import io.github.zero88.msa.bp.http.server.rest.RestApi;
import io.github.zero88.msa.bp.micro.MicroContext;
import io.github.zero88.msa.bp.micro.MicroserviceProvider;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.HttpLocation;

public class MockHttpServiceServer extends ContainerVerticle {

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
            throw new BlueprintException("error");
        }

    }

}
