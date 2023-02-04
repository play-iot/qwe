package cloud.playio.qwe.http.server.upload;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.DeliveryEvent;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.http.EventMethodDefinition;
import cloud.playio.qwe.http.EventMethodMapping;
import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.UploadSystem;
import cloud.playio.qwe.http.server.RoutePath;
import cloud.playio.qwe.http.server.RouterCreator;
import cloud.playio.qwe.http.server.config.FileUploadConfig;
import cloud.playio.qwe.http.server.handler.HttpEBDispatcher;

import lombok.NonNull;

public final class UploadRouterCreator implements RouterCreator<FileUploadConfig>, UploadSystem {

    @Override
    public Function<HttpServerConfig, FileUploadConfig> lookupConfig() {
        return HttpServerConfig::getFileUploadConfig;
    }

    @Override
    public Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                            @NonNull FileUploadConfig config) {
        final String uploadDir = FileUtils.createFolder(pluginDir, config.getUploadDir());
        final Router router = Router.router(sharedData.getVertx());
        config.getListenerClasses()
              .stream()
              .map(UploadListener::create)
              .filter(Objects::nonNull)
              .forEach(listener -> createRoute(sharedData, config, uploadDir, router, listener));
        return router;
    }

    private void createRoute(SharedDataLocalProxy sharedData, FileUploadConfig config, String uploadDir, Router router,
                             UploadListener listener) {
        EventBusClient.create(sharedData).register(listener.address(), listener);
        long limit = HttpServerConfig.MB *
                     (listener.maxUploadSize() <= 0 ? config.getMaxBodySizeMB() : listener.maxUploadSize());
        for (EventMethodDefinition definition : listener.definitions()) {
            for (EventMethodMapping mapping : definition.getMapping()) {
                HttpEBDispatcher dispatcher = HttpEBDispatcher.create(sharedData.sharedKey(),
                                                                      new DeliveryEvent().address(listener.address())
                                                                                         .action(mapping.getAction()));
                this.createRoute(router, config, RoutePath.create(mapping))
                    .handler(BodyHandler.create(uploadDir).setBodyLimit(limit))
                    .handler(UploadFileHandler.create(config.getHandlerClass())
                                              .setup(dispatcher, mapping.getAuth(), listener.predicate()));
            }
        }
    }

}
