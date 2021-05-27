package io.zero88.qwe;

import lombok.NonNull;

/**
 * Represents for a Verticle {@code component} that keeps a single duty function.
 *
 * @param <C> Type of Config
 * @param <T> Type of Component Context
 * @see IConfig
 * @see ComponentContext
 * @see HasConfig
 * @see ComponentVerticle
 */
public interface Component<C extends IConfig, T extends ComponentContext> extends QWEVerticle<C> {

    /**
     * Component name
     *
     * @return a component name
     */
    @Override
    default String appName() {
        return this.getClass().getName();
    }

    /**
     * Deployment hook when application install component
     *
     * @return hook
     * @see DeployHook
     * @see Application#installComponents()
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
     * Component context
     *
     * @return ComponentContext
     */
    @NonNull T getContext();

}
