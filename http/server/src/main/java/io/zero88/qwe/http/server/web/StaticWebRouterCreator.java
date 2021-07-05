package io.zero88.qwe.http.server.web;

import java.nio.file.Path;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpLogSystem.WebLogSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.StaticWebConfig;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StaticWebRouterCreator implements RouterCreator<StaticWebConfig>, WebLogSystem {

    private final Path pluginDir;

    @Override
    public @NonNull Router router(@NonNull StaticWebConfig config, @NonNull SharedDataLocalProxy sharedData) {
        final StaticHandler staticHandler = StaticHandler.create();
        if (config.isInResource()) {
            staticHandler.setWebRoot(config.getWebRoot());
        } else {
            String webDir = FileUtils.createFolder(pluginDir, config.getWebRoot());
            logger().info(decor("Register {} route [{}][{}]"), function(), config.getPath(), webDir);
            staticHandler.setEnableRangeSupport(true)
                         .setSendVaryHeader(true)
                         .setFilesReadOnly(false)
                         .setAllowRootFileSystemAccess(true)
                         .setIncludeHidden(false)
                         .setWebRoot(webDir);
        }
        final Router router = Router.router(sharedData.getVertx());
        router.route(BasePaths.addWildcards("/")).handler(staticHandler);
        return router;
    }

}
