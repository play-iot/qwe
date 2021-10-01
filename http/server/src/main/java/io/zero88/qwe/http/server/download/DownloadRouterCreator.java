package io.zero88.qwe.http.server.download;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.EventMethodMapping;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpSystem.DownloadSystem;
import io.zero88.qwe.http.server.RoutePath;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.FileDownloadConfig;
import io.zero88.qwe.http.server.handler.HttpEBDispatcher;

import lombok.NonNull;

public final class DownloadRouterCreator implements RouterCreator<FileDownloadConfig>, DownloadSystem {

    @Override
    public Function<HttpServerConfig, FileDownloadConfig> lookupConfig() {
        return HttpServerConfig::getFileDownloadConfig;
    }

    @Override
    public Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                            @NonNull FileDownloadConfig config) {
        final Path downloadDir = Paths.get(FileUtils.createFolder(pluginDir, config.getDownloadDir()));
        final Router router = Router.router(sharedData.getVertx());
        logger().info(decor("Setup download dir[{}]"), downloadDir);
        config.getListenerClasses()
              .stream()
              .map(DownloadListener::create)
              .filter(Objects::nonNull)
              .map(listener -> listener.setup(downloadDir))
              .forEach(listener -> createRoute(sharedData, config, router, listener));
        return router;
    }

    private void createRoute(SharedDataLocalProxy sharedData, FileDownloadConfig config, Router router,
                             DownloadListener listener) {
        EventBusClient.create(sharedData).register(listener.address(), listener);
        for (EventMethodDefinition definition : listener.definitions()) {
            for (EventMethodMapping mapping : definition.getMapping()) {
                HttpEBDispatcher dispatcher = HttpEBDispatcher.create(sharedData.sharedKey(),
                                                                      new DeliveryEvent().address(listener.address())
                                                                                         .action(mapping.getAction()));
                this.createRoute(router, config, RoutePath.create(mapping, Collections.singletonList("*/*")))
                    .handler(listener.getStaticHandler())
                    .handler(DownloadFileHandler.create(config.getHandlerClass()).setup(dispatcher, mapping.getAuth()));
            }
        }
    }

}
