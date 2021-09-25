package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.http.EventMethodMapping;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpSystem.UploadSystem;
import io.zero88.qwe.http.server.RoutePath;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.FileUploadConfig;

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
              .map(ReflectionClass::findClass)
              .map(UploadListener::create)
              .filter(Objects::nonNull)
              .forEach(listener -> {
                  EventBusClient.create(sharedData).register(listener.address(), listener);
                  long limit = HttpServerConfig.MB *
                               (listener.maxUploadSize() <= 0 ? config.getMaxBodySizeMB() : listener.maxUploadSize());
                  for (EventMethodDefinition definition : listener.definitions()) {
                      for (EventMethodMapping mapping : definition.getMapping()) {
                          this.createRoute(router, config, RoutePath.create(mapping))
                              .handler(BodyHandler.create(uploadDir).setBodyLimit(limit))
                              .handler(UploadFileHandler.create(config.getHandlerClass())
                                                        .setup(sharedData.sharedKey(), mapping.getAuth(),
                                                               new DeliveryEvent().setAddress(listener.address())
                                                                                  .setAction(mapping.getAction()),
                                                               Paths.get(uploadDir), listener.predicate()));
                      }
                  }
              });
        return router;
    }

}
