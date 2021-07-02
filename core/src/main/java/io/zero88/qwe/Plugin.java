package io.zero88.qwe;

import lombok.NonNull;

/**
 * Represents for a {@code Plugin} that keeps a single duty function.
 *
 * @param <C> Type of Plugin Config
 * @param <T> Type of Plugin Context
 * @see PluginConfig
 * @see PluginContext
 * @see PluginVerticle
 */
//FIXME: Rename to Plugin
public interface Plugin<C extends PluginConfig, T extends PluginContext> extends QWEVerticle<C> {

    /**
     * Expresses a functional that this plugin bring to. For example: {@code http-server}, {@code sql-mysql}
     *
     * @return the plugin function name
     * @apiNote To better identify, {@code plugin Function Name} convention is {@code kebab-case}
     */
    @Override
    default String appName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Plugin config
     *
     * @return a plugin config
     */
    C pluginConfig();

    /**
     * Plugin context
     *
     * @return a plugin context
     */
    @NonNull T pluginContext();

    /**
     * Deployment hook when application install plugin
     *
     * @return hook
     * @see PluginDeployHook
     * @see Application#installPlugins()
     */
    @NonNull PluginDeployHook<T> hook();

    /**
     * Register a plugin context after installed plugin successfully
     * <p>
     * This method will be called automatically by system before deploying verticle.
     *
     * @param context shared data key
     * @return a reference to this, so the API can be used fluently
     * @see Application
     */
    T setup(T context);

}
