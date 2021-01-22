package io.github.zero88.qwe.component;

import io.github.zero88.qwe.IConfig;
import io.vertx.core.Verticle;

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
     * Unit context
     *
     * @return ComponentContext
     */
    T getContext();

    /**
     * Register {@code Vertx} local shared data key from {@code Application} to {@code Component}
     * <p>
     * This method will be called automatically by system before deploying verticle.
     *
     * @param sharedKey shared data key
     * @param <U>       Type of Component Verticle
     * @return a reference to this, so the API can be used fluently
     * @see Application
     */
    <U extends Component<C, T>> U registerSharedKey(String sharedKey);

    /**
     * Retrieve {@code Vertx} shared data value by key data
     *
     * @param <D>     Data value type
     * @param dataKey given data key
     * @return Data value. {@code nullable} if no data value by key or data value type doesn't match type with expected
     *     value
     */
    default <D> D getSharedData(String dataKey) {
        return getSharedData(dataKey, null);
    }

    /**
     * Retrieve {@code Vertx} shared data value by key data. If no data value by key or data value type doesn't match
     * type with expected value, it will fallback to given value.
     *
     * @param <D>      T type of data value
     * @param dataKey  given data key
     * @param fallback Fallback value
     * @return Data value.
     */
    <D> D getSharedData(String dataKey, D fallback);

    /**
     * Add {@code Vertx} shared data value by key data. If no data value by key or data value type doesn't match type
     * with expected value, it will fallback to given value.
     *
     * @param <D>     T type of data value
     * @param dataKey given data key
     * @param data    Data value
     * @return Data value.
     */
    <D> D addSharedData(String dataKey, D data);

}
