package io.github.zero88.qwe.http.server.web;

import io.github.zero88.qwe.component.SharedDataLocalProxy;
import io.github.zero88.qwe.http.server.BasePaths;
import io.github.zero88.qwe.http.server.HttpConfig.StaticWebConfig;
import io.github.zero88.qwe.http.server.HttpLogSystem.WebLogSystem;
import io.github.zero88.qwe.http.server.RouterCreator;
import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import lombok.NonNull;

public class StaticWebRouterCreator implements RouterCreator<StaticWebConfig>, WebLogSystem {

    @Override
    public @NonNull Router router(@NonNull StaticWebConfig config, @NonNull SharedDataLocalProxy sharedData) {
        final StaticHandler staticHandler = StaticHandler.create();
        if (config.isInResource()) {
            staticHandler.setWebRoot(config.getWebRoot());
        } else {
            String webDir = FileUtils.createFolder(sharedData.getData(SharedDataLocalProxy.APP_DATADIR),
                                                   config.getWebRoot());
            log().info(decor("Registering route '{}' with web dir '{}'"), config.getWebPath(), webDir);
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
