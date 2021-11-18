package io.zero88.qwe.http.server.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.authorization.MicroProfileAuthorization;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.zero88.qwe.JsonHelper.Junit4;
import io.zero88.qwe.KeyStoreProvider;
import io.zero88.qwe.TestHelper;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.http.server.HttpServerPluginProvider;
import io.zero88.qwe.http.server.HttpServerPluginTestBase;
import io.zero88.qwe.http.server.HttpServerRouter;

public class HttpJWTServerTest extends HttpServerPluginTestBase {

    private JWTAuth authProvider;
    private Map<String, String> users = new HashMap<>();

    @Before
    public void before(TestContext context) {
        super.before(context);
        final KeyStoreProvider jwt = KeyStoreProvider.jwt();
        //TODO fix it
        authProvider = JWTAuth.create(vertx, new JWTAuthOptions().setJWTOptions(
            new JWTOptions().setAlgorithm("ES512").setExpiresInMinutes(10)));
        users.put("zero88", "123");
        users.put("xyz", "123");
        deploy(vertx, context, httpConfig, initProvider());
    }

    @Override
    public HttpServerPluginProvider initProvider() {
        final HttpServerRouter router = new HttpServerRouter();
        final JWTAuthHandler authHandler = JWTAuthHandler.create(authProvider);
        router.addCustomBuilder((vertx, r, config, context) -> {
            r.route("/login").handler(ctx -> {
                String username = ctx.request().getParam("username");
                String password = ctx.request().getParam("password");
                if ("zero88".equals(username) && "123".equals(password)) {
                    ctx.response()
                       .end(authProvider.generateToken(new JsonObject().put("sub", "zero88")
                                                                       .put("permissions",
                                                                            Collections.singletonList("halo"))));
                } else {
                    ctx.fail(401);
                }
            });
            r.route("/secure/*").handler(authHandler);
            r.route("/secure/page").handler(ctx -> {
                TestHelper.LOGGER.info("USER Attr[{}]-Principal[{}]-Authorization[{}]", ctx.user().attributes(),
                                       ctx.user().principal(), ctx.user().authorizations());
                ctx.response().end("hello, " + ctx.user().get("sub"));
            });
            r.route("/secure/authorization")
             .handler(AuthorizationHandler.create(PermissionBasedAuthorization.create("halo"))
                                          .addAuthorizationProvider(MicroProfileAuthorization.create()))
             .handler(ctx -> {
                 TestHelper.LOGGER.info("USER Attr[{}]-Principal[{}]-Authorization[{}]", ctx.user().attributes(),
                                        ctx.user().principal(), ctx.user().authorizations());
                 ctx.response().end("hi, " + ctx.user().get("sub"));
             });
            return r;
        });
        return new HttpServerPluginProvider(router);
    }

    @Test
    public void test_without_login(TestContext context) {
        Async async = context.async();
        client.request(requestOptions().setURI("/secure/page"), null, true).onSuccess(resp -> {
            System.out.println(resp.headers());
            Assert.assertEquals(401, resp.getStatus().code());
            Assert.assertTrue(resp.isError());
            Junit4.assertJson(context, async, new JsonObject(
                "{\"message\":\"UNKNOWN_ERROR | Cause(Unauthorized)\",\"code\":\"UNKNOWN_ERROR\"}"), resp.body());
        }).onFailure(context::fail);
    }

    @Test
    public void test_with_login(TestContext context) {
        Async async = context.async();
        client.request(requestOptions().setURI("/login"), RequestData.builder()
                                                                     .filter(new JsonObject().put("username", "zero88")
                                                                                             .put("password", "123"))
                                                                     .build()).flatMap(resp -> {
            String token = new TokenCredentials(resp.body().getString("data")).toHttpAuthorization();
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            TestHelper.LOGGER.info("TOKEN: {}", token);
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            return client.request(requestOptions().setURI("/secure/page"),
                                  RequestData.builder().headers(new JsonObject().put("Authorization", token)).build());
        }).onSuccess(resp -> {
            System.out.println(resp.headers());
            Assert.assertFalse(resp.isError());
            Junit4.assertJson(context, async, new JsonObject().put("data", "hello, zero88"), resp.body());
        }).onFailure(context::fail);
    }

    @Test
    public void test_with_login_with_authorization(TestContext context) {
        Async async = context.async();
        client.request(requestOptions().setURI("/login"), RequestData.builder()
                                                                     .filter(new JsonObject().put("username", "zero88")
                                                                                             .put("password", "123"))
                                                                     .build()).flatMap(resp -> {
            String token = new TokenCredentials(resp.body().getString("data")).toHttpAuthorization();
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            TestHelper.LOGGER.info("TOKEN: {}", token);
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            return client.request(requestOptions().setURI("/secure/authorization"),
                                  RequestData.builder().headers(new JsonObject().put("Authorization", token)).build());
        }).onSuccess(resp -> {
            System.out.println(resp.headers());
            Assert.assertFalse(resp.isError());
            Junit4.assertJson(context, async, new JsonObject().put("data", "hi, zero88"), resp.body());
        }).onFailure(context::fail);
    }

}
