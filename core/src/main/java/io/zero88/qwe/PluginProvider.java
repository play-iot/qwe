package io.zero88.qwe;

import java.util.Collection;
import java.util.Collections;

/**
 * A plugin provider
 *
 * @param <T> Plugin type
 */
public interface PluginProvider<T extends Plugin> {

    Class<T> pluginClass();

    T provide(SharedDataLocalProxy sharedData);

    /**
     * Provide a set of dependency extensions that used by plugin
     *
     * @return set of extensions classes
     */
    default Collection<Class<? extends Extension>> extensions() {
        return Collections.emptySet();
    }

}
