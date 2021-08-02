package io.zero88.qwe.http.server.web;

import java.nio.file.Path;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.http.server.BasePaths;
import io.zero88.qwe.http.server.HttpSystem.WebSystem;
import io.zero88.qwe.http.server.RouterCreator;
import io.zero88.qwe.http.server.config.StaticWebConfig;

import lombok.NonNull;

public final class StaticWebRouterCreator implements RouterCreator<StaticWebConfig>, WebSystem {

    @Override
    public @NonNull Router subRouter(@NonNull Path pluginDir, @NonNull StaticWebConfig config,
                                     @NonNull SharedDataLocalProxy sharedData) {
        final StaticHandler staticHandler = StaticHandler.create();
        if (config.isInResource()) {
            staticHandler.setWebRoot(config.getWebRoot());
        } else {
            String webDir = FileUtils.createFolder(pluginDir, config.getWebRoot());
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
