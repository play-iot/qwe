package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.zero88.utils.FileUtils;
import io.github.zero88.utils.Strings;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.HttpServerConfig;
import io.zero88.qwe.http.server.HttpServerPlugin;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.ServerInfo;
import io.zero88.qwe.http.server.config.FileUploadConfig;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UploadRouterCreator implements RouterCreator<FileUploadConfig> {

    private final Path pluginDir;

    @Override
    public Router router(@NonNull FileUploadConfig config, @NonNull SharedDataLocalProxy sharedData) {
        final String uploadDir = FileUtils.createFolder(pluginDir, config.getUploadDir());
        final EventBusClient eventbus = EventBusClient.create(sharedData);
        final ServerInfo serverInfo = sharedData.getData(HttpServerPlugin.SERVER_INFO_DATA_KEY);
        final UploadListener listener = UploadListener.create(sharedData, config.getListenerClass());
        final String address = Strings.fallback(config.getListenerAddress(), listener.getClass().getName());
        final UploadFileHandler handler = UploadFileHandler.create(config.getHandlerClass(), eventbus, address,
                                                                   Paths.get(uploadDir), serverInfo.getPublicHost());
        eventbus.register(address, listener);
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
