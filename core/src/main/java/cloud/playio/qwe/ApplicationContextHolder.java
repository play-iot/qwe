package cloud.playio.qwe;

import java.util.Collection;

import org.jetbrains.annotations.Nullable;

/**
 * Application context holder
 */
public interface ApplicationContextHolder extends ExtensionHolder {

    /**
     * Get list of plugin context
     *
     * @return plugin context
     */
    Collection<PluginContext> plugins();

    /**
     * Lookup plugin context by its class
     *
     * @param pluginContextCls plugin context class
     * @param <T>              Type of plugin context
     * @return plugin context or {@code null} if not found
     */
    @Nullable <T extends PluginContext> T plugin(Class<T> pluginContextCls);

}
