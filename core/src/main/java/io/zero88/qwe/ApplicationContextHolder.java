package io.zero88.qwe;

import org.jetbrains.annotations.Nullable;

/**
 * Application context holder
 */
@SuppressWarnings("rawtypes")
public interface ApplicationContextHolder {

    /**
     * Lookup plugin context by its class
     *
     * @param pluginContextCls plugin context class
     * @param <T>              Type of plugin context
     * @return plugin context or {@code null} if not found
     */
    @Nullable <T extends PluginContext> T plugin(Class<T> pluginContextCls);

    /**
     * Lookup extension entrypoint by extension class
     *
     * @param <EE>         Type of extension entrypoint
     * @param extensionCls extension class
     * @return extension entrypoint
     */
    @Nullable <EE extends ExtensionEntrypoint> EE extension(Class<? extends Extension> extensionCls);

}
