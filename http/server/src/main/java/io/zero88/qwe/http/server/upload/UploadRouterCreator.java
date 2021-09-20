package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Strings;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpSystem.UploadSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.FileUploadConfig;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;

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
        final UploadListener listener = UploadListener.create(config.getListenerClass());
        final String address = Strings.fallback(config.getListenerAddress(), listener.getClass().getName());
        final UploadFileHandler handler = UploadFileHandler.create(config.getHandlerClass())
                                                           .setup(sharedData.sharedKey(), address,
                                                                  Paths.get(uploadDir));
        EventBusClient.create(sharedData).register(address, listener);
        final Router router = Router.router(sharedData.getVertx());
        router.post(config.getPath())
              .handler(BodyHandler.create(uploadDir).setBodyLimit(config.getMaxBodySizeMB() * HttpServerConfig.MB))
              .handler(handler)
              .handler(new EventMessageResponseHandler())
              .produces(HttpUtils.JSON_CONTENT_TYPE)
              .produces(HttpUtils.JSON_UTF8_CONTENT_TYPE);
        return router;
    }

}
