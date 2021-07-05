package io.zero88.qwe.http.server.download;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpLogSystem.DownloadLogSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.FileDownloadConfig;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadRouterCreator implements RouterCreator<FileDownloadConfig>, DownloadLogSystem {

    private final Path pluginDir;

    @Override
    public Router router(@NonNull FileDownloadConfig config, @NonNull SharedDataLocalProxy sharedData) {
        String downloadDir = FileUtils.createFolder(pluginDir, config.getDownloadDir());
        logger().info(decor("Register {} route [{}][{}]"), function(), config.getPath(), downloadDir);
        final Router router = Router.router(sharedData.getVertx());
        router.get(BasePaths.addWildcards("/"))
              .handler(StaticHandler.create()
                                    .setEnableRangeSupport(true)
                                    .setSendVaryHeader(true)
                                    .setFilesReadOnly(false)
                                    .setAllowRootFileSystemAccess(true)
                                    .setIncludeHidden(false)
                                    .setWebRoot(downloadDir))
              .handler(DownloadFileHandler.create(config.getHandlerClass(), config.getPath(), Paths.get(downloadDir)));
        return router;
    }

}
