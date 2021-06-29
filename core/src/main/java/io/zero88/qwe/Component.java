package io.zero88.qwe;

import lombok.NonNull;

/**
 * Represents for a Verticle {@code component} that keeps a single duty function.
 *
 * @param <C> Type of Config
 * @param <T> Type of Component Context
 * @see ComponentConfig
 * @see ComponentContext
 * @see ComponentVerticle
 */
//FIXME: Rename to Plugin
public interface Component<C extends ComponentConfig, T extends ComponentContext> extends QWEVerticle<C> {

    /**
     * Expresses a functional that this component bring to. For example: {@code http-server}, {@code sql-mysql}
     *
     * @return the component function name
     * @apiNote To better identify, {@code Component Function Name} convention is {@code kebab-case}
     */
    @Override
    default String appName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Component config
     *
     * @return a component config
     */
    C componentConfig();

    /**
     * Component context
     *
     * @return ComponentContext
     */
    @NonNull T componentContext();

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

}
