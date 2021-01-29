package io.github.zero88.qwe.http.server.download;

import java.nio.file.Path;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.http.server.BasePaths;
import io.github.zero88.qwe.http.server.HttpConfig.FileStorageConfig.DownloadConfig;
import io.github.zero88.qwe.http.server.RouterCreator;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class DownloadRouterCreator implements RouterCreator<DownloadConfig> {

    private final Path storageDir;

    @Override
    public Router router(@NonNull DownloadConfig config, @NonNull SharedDataLocalProxy sharedData) {
        log.info("Init Download router: '{}'...", config.getPath());
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
