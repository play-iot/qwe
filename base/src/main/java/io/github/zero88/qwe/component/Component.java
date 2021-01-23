package io.github.zero88.qwe.component;

import io.github.zero88.qwe.IConfig;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;

import lombok.NonNull;

/**
 * Represents small and independent component that integrate with verticle.
 *
 * @param <C> Type of Config
 * @param <T> Type of Component Context
 * @see IConfig
 * @see ComponentContext
 * @see HasConfig
 * @see ComponentVerticle
 */
public interface Component<C extends IConfig, T extends ComponentContext> extends HasConfig<C>, Verticle {

    /**
     * Deployment hook when application install component
     *
     * @return hook
     * @see DeployHook
     * @see Application#installComponents(Promise)
     */
    @NonNull DeployHook<T> hook();

    /**
     * Register a component context after installed component successfully
     * <p>
     * This method will be called automatically by system before deploying verticle.
     *
     * @param context shared data key
     * @return a reference to this, so the API can be used fluently
     * @see Application
     */
    T setup(T context);

    /**
     * Get shared data proxy
     *
     * @return shared data proxy
     * @see SharedDataLocalProxy
     */
    @NonNull SharedDataLocalProxy sharedData();

    /**
     * Component context
     *
     * @return ComponentContext
     */
    @NonNull T getContext();

}
