package cloud.playio.qwe.http.server.rest;

import java.nio.file.Path;
import java.util.function.Function;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.Urls;
import io.vertx.ext.web.Router;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.exceptions.InitializerError;
import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.ApisSystem;
import cloud.playio.qwe.http.server.RouterCreator;
import cloud.playio.qwe.http.server.config.ApiConfig;

import lombok.NonNull;

public final class ProxyServiceApisCreator implements RouterCreator<ApiConfig>, ApisSystem {

    @Override
    public String function() {
        return "Proxy-Service-API";
    }

    @Override
    public @NonNull String mountPoint(@NonNull ApiConfig config) {
        return config.getProxyConfig().getPath();
    }

    @Override
    public String routePath(@NonNull ApiConfig config) {
        return Urls.combinePath(config.getPath(), mountPoint(config));
    }

    @Override
    public boolean validate(ApiConfig config) {
        return config.getProxyConfig().isEnabled();
    }

    @Override
    public Function<HttpServerConfig, ApiConfig> lookupConfig() {
        return HttpServerConfig::getApiConfig;
    }

    @Override
    public @NonNull Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                                     @NonNull ApiConfig config) {
        if (!ReflectionClass.hasClass("cloud.playio.qwe.micro.DiscoveryContext")) {
            throw new InitializerError("To enabled dynamic route, you have to use on qwe-micro-discovery.jar plugin");
        }
        Router proxyRouter = Router.router(sharedData.getVertx());
        proxyRouter.route(RouterCreator.addWildcards(config.getProxyConfig().getPath())).disable();
        return proxyRouter;
    }

}
