package io.zero88.qwe;

/**
 * A plugin provider
 *
 * @param <T> Plugin type
 */
public interface PluginProvider<T extends Plugin> {

    Class<T> pluginClass();

    T provide(SharedDataLocalProxy sharedData);

}
