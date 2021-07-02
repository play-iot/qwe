package io.zero88.qwe;

/**
 * Plugin Provider
 *
 * @param <T> Plugin type
 */
public interface PluginProvider<T extends Plugin> {

    Class<T> pluginClass();

    T provide(SharedDataLocalProxy proxy);

}
