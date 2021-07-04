package io.zero88.qwe.http.server.download;

import java.nio.file.Path;

import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.config.FileDownloadConfig;
import io.zero88.qwe.http.server.HttpLogSystem.DownloadLogSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadRouterCreator implements RouterCreator<FileDownloadConfig>, DownloadLogSystem {

    private final Path storageDir;

    @Override
    public Router router(@NonNull FileDownloadConfig config, @NonNull SharedDataLocalProxy sharedData) {
        logger().info(decor("Registering route: '{}' in storage '{}'..."), config.getPath(), storageDir);
        final Router router = Router.router(sharedData.getVertx());
        router.get(BasePaths.addWildcards("/"))
              .handler(StaticHandler.create()
                                    .setEnableRangeSupport(true)
                                    .setSendVaryHeader(true)
                                    .setFilesReadOnly(false)
                                    .setAllowRootFileSystemAccess(true)
                                    .setIncludeHidden(false)
                                    .setWebRoot(storageDir.toString()))
              .handler(DownloadFileHandler.create(config.getHandlerClass(), config.getPath(), storageDir));
        return router;
    }

}
