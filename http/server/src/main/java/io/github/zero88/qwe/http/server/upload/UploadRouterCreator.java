package io.github.zero88.qwe.http.server.upload;

import static io.github.zero88.qwe.http.server.RouterConfig.MB;

import java.nio.file.Path;
import java.util.ArrayList;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.event.EventAction;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.http.HttpUtils;
import io.github.zero88.qwe.http.server.HttpConfig.FileStorageConfig.UploadConfig;
import io.github.zero88.qwe.http.server.HttpLogSystem.UploadLogSystem;
import io.github.zero88.qwe.http.server.RouterCreator;
import io.github.zero88.qwe.http.server.handler.EventMessageResponseHandler;
import io.github.zero88.utils.Strings;
import io.reactivex.annotations.Nullable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadRouterCreator implements RouterCreator<UploadConfig>, UploadLogSystem {

    private final Path storageDir;
    private final String publicUrl;

    @Nullable
    @Override
    public Router router(@NonNull UploadConfig config, @NonNull SharedDataLocalProxy sharedData) {
        log().info(decor("Registering route: '{}' in storage '{}'..."), config.getPath(), storageDir);
        final EventbusClient eventbus = EventbusClient.create(sharedData);
        final String address = Strings.fallback(config.getListenerAddress(), sharedData.getSharedKey() + ".upload");
        final EventModel listenerEvent = EventModel.builder()
                                                   .address(address)
                                                   .event(EventAction.CREATE)
                                                   .pattern(EventPattern.REQUEST_RESPONSE)
                                                   .local(true)
                                                   .build();
        final UploadListener listener = UploadListener.create(sharedData, config.getListenerClass(),
                                                              new ArrayList<>(listenerEvent.getEvents()));
        final UploadFileHandler handler = UploadFileHandler.create(config.getHandlerClass(), eventbus, listenerEvent,
                                                                   storageDir, publicUrl);
        eventbus.register(address, listener);
        final Router router = Router.router(sharedData.getVertx());
        router.post(config.getPath())
              .handler(BodyHandler.create(storageDir.toString()).setBodyLimit(config.getMaxBodySizeMB() * MB))
              .handler(handler)
              .handler(new EventMessageResponseHandler())
              .produces(HttpUtils.JSON_CONTENT_TYPE)
              .produces(HttpUtils.JSON_UTF8_CONTENT_TYPE);
        return router;
    }

}
