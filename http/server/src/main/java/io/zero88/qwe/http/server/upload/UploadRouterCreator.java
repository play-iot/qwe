package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.HttpConfig.FileStorageConfig.UploadConfig;
import io.zero88.qwe.http.server.HttpLogSystem.UploadLogSystem;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadRouterCreator implements RouterCreator<UploadConfig>, UploadLogSystem {

    private final Path storageDir;
    private final String publicUrl;

    @Override
    public Router router(@NonNull UploadConfig config, @NonNull SharedDataLocalProxy sharedData) {
        log().info(decor("Registering route: '{}' in storage '{}'..."), config.getPath(), storageDir);
        final EventBusClient eventbus = EventBusClient.create(sharedData);
        final String address = Strings.fallback(config.getListenerAddress(), sharedData.sharedKey() + ".upload");
        final UploadListener listener = UploadListener.create(sharedData, config.getListenerClass());
        final UploadFileHandler handler = UploadFileHandler.create(config.getHandlerClass(), eventbus, address,
                                                                   storageDir, publicUrl);
        eventbus.register(address, listener);
        final Router router = Router.router(sharedData.getVertx());
        router.post(config.getPath())
              .handler(
                  BodyHandler.create(storageDir.toString()).setBodyLimit(config.getMaxBodySizeMB() * RouterConfig.MB))
              .handler(handler)
              .handler(new EventMessageResponseHandler())
              .produces(HttpUtils.JSON_CONTENT_TYPE)
              .produces(HttpUtils.JSON_UTF8_CONTENT_TYPE);
        return router;
    }

}
