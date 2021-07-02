package io.zero88.qwe;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PluginContextLookup {

    /**
     * Lookup plugin context by its class
     *
     * @param aClass plugin context class
     * @param <T>    Type of plugin context
     * @return plugin context or {@code null} if not found
     */
    @Nullable <T extends PluginContext> T query(Class<T> aClass);

}
