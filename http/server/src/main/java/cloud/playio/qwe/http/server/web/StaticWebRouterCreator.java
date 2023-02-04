package cloud.playio.qwe.http.server.web;

import java.nio.file.Path;
import java.util.function.Function;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.http.server.HttpServerConfig;
import cloud.playio.qwe.http.server.HttpSystem.WebSystem;
import cloud.playio.qwe.http.server.RouterCreator;
import cloud.playio.qwe.http.server.config.StaticWebConfig;

import lombok.NonNull;

public final class StaticWebRouterCreator implements RouterCreator<StaticWebConfig>, WebSystem {

    @Override
    public Function<HttpServerConfig, StaticWebConfig> lookupConfig() {
        return HttpServerConfig::getStaticWebConfig;
    }

    @Override
    public @NonNull Router subRouter(@NonNull SharedDataLocalProxy sharedData, @NonNull Path pluginDir,
                                     @NonNull StaticWebConfig config) {
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
        router.route(RouterCreator.addWildcards("/")).handler(staticHandler);
        return router;
    }

}
