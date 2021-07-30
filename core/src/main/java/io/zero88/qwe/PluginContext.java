package io.zero88.qwe;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import io.zero88.qwe.PluginConfig.PluginDirConfig;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Plugin context after deployment
 *
 * @see Plugin
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface PluginContext extends HasAppName, HasPluginName, HasSharedKey, ExtensionHolder {

    /**
     * Create a pre-context of plugin before deploying it
     *
     * @param appName    an associate app name
     * @param pluginName an associate plugin name
     * @param sharedKey  a shared key to access local data in {@code Application}
     * @param dataDir    a current application data dir
     * @param extensions the dependency extensions
     * @return a plugin pre-context
     */
    static PluginContext create(String appName, String pluginName, String sharedKey, Path dataDir,
                                Collection<? extends Extension> extensions) {
        return new DefaultPluginContext(appName, pluginName, dataDir, sharedKey, extensions);
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

    /**
     * Inject a plugin deployment id in post deployment
     *
     * @param deployId deployment id
     * @return a reference to this for fluent API
     */
    PluginContext deployId(String deployId);

    @Getter
    @Accessors(fluent = true)
    class DefaultPluginContext implements PluginContext {

        private final String appName;
        private final String pluginName;
        private final Path dataDir;
        private final String sharedKey;
        private final Map<Class<? extends Extension>, Extension> extensionMap;
        @Setter
        private String deployId;

        protected DefaultPluginContext(@NonNull PluginContext ctx) {
            this(ctx.appName(), ctx.pluginName(), ctx.dataDir(), ctx.sharedKey(), ctx.extensions());
            this.deployId = ctx.deployId();
        }

        DefaultPluginContext(String appName, String pluginName, Path dataDir, String sharedKey,
                             Collection<? extends Extension> extensions) {
            this.appName = appName;
            this.pluginName = pluginName;
            this.dataDir = dataDir;
            this.sharedKey = sharedKey;
            this.extensionMap = extensions.stream().collect(Collectors.toMap(Extension::getClass, Function.identity()));
        }

        @Override
        public final Collection<Extension> extensions() {
            return extensionMap.values();
        }

        @Override
        public final <E extends Extension> E getExtension(Class<E> extCls) {
            return (E) extensionMap.get(extCls);
        }

    }

}
