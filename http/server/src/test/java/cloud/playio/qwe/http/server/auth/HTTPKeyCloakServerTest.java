package cloud.playio.qwe.http.server.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.github.zero88.utils.HttpScheme;
import io.github.zero88.utils.Strings;
import io.github.zero88.utils.Urls;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.jwt.authorization.MicroProfileAuthorization;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import cloud.playio.qwe.JsonHelper;
import cloud.playio.qwe.PluginDeploymentHelper;
import cloud.playio.qwe.TestHelper;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.http.server.HttpServerPluginProvider;
import cloud.playio.qwe.http.server.HttpServerPluginTestBase;
import cloud.playio.qwe.http.server.HttpServerRouter;

public class HTTPKeyCloakServerTest extends HttpServerPluginTestBase {

    OAuth2Auth oauth2;

    @Before
    public void before(TestContext context) {
        super.before(context);
        Async flag = context.async();
        String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                           "MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQBwP6DBhXystMGRHIsw9hM8s0l4WPrSS4w9HTVtTb1HessUV2s5VIDGIcQ1F6ZsTvCkzv+HXIPcPpZ1ArszSF0AVYAsWYKbjn9MKjIf1UjWkuQmS+WFyz3mdTVyTdsCLvolJZpUBnBVT/Dr5Xu2CK0ktvkBOlD00ANQhHRMNRBvCVDi54=\n" +
                           "-----END PUBLIC KEY-----";
        JsonObject c = new JsonObject().put("realm", "vertx")
                                       /*.put("realm-public-key", publicKey)*/.put("auth-server-url",
                                                                                   "http://localhost:8180/auth")
                                       .put("ssl-required", "external")
                                       .put("resource", "vertx-client")
                                       .put("credentials",
                                            new JsonObject().put("secret", "fca024ee-1018-4aad-b28c-593febdb4174"));

        // Initialize the OAuth2 Library
        KeycloakAuth.discover(vertx, new OAuth2Options().setFlow(OAuth2FlowType.PASSWORD).setClientId("vertx")
                                                        //                                                        .setClientSecret("fca024ee-1018-4aad-b28c-593febdb4174")
                                                        .setSite("http://localhost:8180/auth/realms/vertx")/*
                                                        .setTenant("vertx")*/).onSuccess(oauth2 -> {
            this.oauth2 = oauth2;
        }).onComplete(r -> flag.countDown());
        //        oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.AUTH_CODE, c);
        PluginDeploymentHelper.Junit4.create(this).deploy(vertx, context, httpConfig, initProvider());
    }

    @Override
    public HttpServerPluginProvider initProvider() {
        final HttpServerRouter router = new HttpServerRouter();
        router.addCustomBuilder((vertx, r, config, context) -> {
            String callbackURL = Urls.optimizeURL(Urls.buildURL(HttpScheme.HTTP, "localhost", httpConfig.getPort()),
                                                  "/callback");
            OAuth2AuthHandler authHandler = OAuth2AuthHandler.create(vertx, oauth2, callbackURL)
                                                             .setupCallback(r.get("/callback"));
            r.route("/login").handler(ctx -> {
                String username = ctx.request().getParam("username");
                String password = ctx.request().getParam("password");
                oauth2.authenticate(new JsonObject().put("username", username).put("password", password))
                      .onSuccess(user -> {
                          //                    AuthorizationProvider authz = KeycloakAuthorization.create();
                          //                    authz.getAuthorizations(user).onSuccess();
                          ctx.response().end(user.principal().toBuffer());
                      })
                      .onFailure(t -> ctx.fail(401, t));
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
    @Ignore
    public void test_with_login(TestContext context) {
        Async async = context.async();
        RequestData req = RequestData.builder()
                                     .filter(new JsonObject().put("username", "test").put("password", "123"))
                                     .build();
        client.request(requestOptions().setMethod(HttpMethod.POST).setURI("/login"), req).flatMap(resp -> {
            TestHelper.LOGGER.info("RESPONSE: {}", resp.toJson());
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            String token = new TokenCredentials(resp.body().getString("data")).toHttpAuthorization();
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            TestHelper.LOGGER.info("TOKEN: {}", token);
            TestHelper.LOGGER.info(Strings.duplicate("=", 50));
            return client.request(requestOptions().setURI("/secure/page"),
                                  RequestData.builder().headers(new JsonObject().put("Authorization", token)).build());
        }).onSuccess(resp -> {
            System.out.println(resp.headers());
            Assert.assertFalse(resp.isError());
            JsonHelper.Junit4.assertJson(context, async, new JsonObject().put("data", "hello, zero88"), resp.body());
        }).onFailure(context::fail);
    }

}

