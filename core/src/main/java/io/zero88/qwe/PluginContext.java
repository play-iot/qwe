package io.zero88.qwe;

import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import io.zero88.qwe.PluginConfig.PluginDirConfig;

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
public interface PluginContext extends HasAppName, HasPluginName, HasSharedKey {

    /**
     * Create a plugin pre-context before deploying plugin
     *
     * @param appName    an associate app name
     * @param pluginName an associate plugin name
     * @param sharedKey  a shared key to access local data in {@code Application}
     * @param dataDir    a current application data dir
     * @return a plugin pre-context
     */
    static PluginContext createPreContext(String appName, String pluginName, String sharedKey, Path dataDir) {
        return new DefaultPluginContext(appName, pluginName, dataDir, sharedKey, null);
    }

    /**
     * Create plugin post-context after deployed plugin
     *
     * @param preContext an associate app name
     * @param deployId   a plugin deployment id
     * @return a plugin post-context
     */
    static PluginContext createPostContext(PluginContext preContext, String deployId) {
        return new DefaultPluginContext(preContext, deployId);
    }

    /**
     * A runtime plugin data dir
     *
     * @return an actual plugin data dir. It can be null if a {@code plugin} does not define plugin dir configuration
     * @see QWEAppConfig#dataDir()
     * @see PluginDirConfig
     */
    @Nullable Path dataDir();

    /**
     * A Plugin deployment id
     *
     * @return deployment id if plugin is already installed successfully, otherwise it is {@code null}
     */
    @Nullable String deployId();

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class DefaultPluginContext implements PluginContext {

        private final String appName;
        private final String pluginName;
        private final Path dataDir;
        private final String sharedKey;
        private final String deployId;

        DefaultPluginContext(@NonNull PluginContext ctx, String deployId) {
            this(ctx.appName(), ctx.pluginName(), ctx.dataDir(), ctx.sharedKey(), deployId);
        }

        protected DefaultPluginContext(@NonNull PluginContext ctx) {
            this(ctx.appName(), ctx.pluginName(), ctx.dataDir(), ctx.sharedKey(), ctx.deployId());
        }

    }

}
