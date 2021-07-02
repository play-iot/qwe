package io.zero88.qwe;

import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Plugin context after deployment
 *
 * @see Plugin
 */
public interface PluginContext extends HasAppName, HasPluginName {

    /**
     * Create default plugin context
     *
     * @param appName    an associate app name
     * @param pluginName an associate plugin name
     * @param dataDir    a current application data dir
     * @param sharedKey  a key to access shared data from {@code Application}
     * @param deployId   a deployment id
     * @return pluginContext
     */
    static PluginContext create(String appName, String pluginName, Path dataDir, String sharedKey, String deployId) {
        return new DefaultPluginContext(appName, pluginName, dataDir, sharedKey, deployId);
    }

    @NonNull Path dataDir();

    @NonNull String sharedKey();

    @NonNull String deployId();

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class DefaultPluginContext implements PluginContext {

        private final String appName;
        private final String pluginName;
        private final Path dataDir;
        private final String sharedKey;
        private final String deployId;

        protected DefaultPluginContext(@NonNull PluginContext ctx) {
            this(ctx.appName(), ctx.pluginName(), ctx.dataDir(), ctx.sharedKey(), ctx.deployId());
        }

    }

}
