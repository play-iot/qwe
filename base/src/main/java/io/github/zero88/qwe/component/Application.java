package io.github.zero88.qwe.component;

import java.util.function.Consumer;

import io.github.zero88.qwe.CarlConfig;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventbusClient;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;

/**
 * Represents a container consists a list of {@code Verticle component} to startup application
 *
 * @see Component
 * @see HasConfig
 * @see ApplicationVerticle
 */
public interface Application extends HasConfig<CarlConfig>, Verticle {

    @Override
    default Class<CarlConfig> configClass() {
        return CarlConfig.class;
    }

    @Override
    default String configFile() {
        return "config.json";
    }

    default String getSharedKey() {
        return this.getClass().getName();
    }

    /**
     * Register eventbus consumer
     *
     * @param eventClient EventController
     * @see EventbusClient#register(EventModel, EventListener)
     * @see EventbusClient#register(String, EventListener)
     * @see EventbusClient#register(String, boolean, EventListener)
     */
    void registerEventbus(EventbusClient eventClient);

    /**
     * Add local shared data to between different verticles
     *
     * @param key  Data key
     * @param data Data value
     * @return a reference to this, so the API can be used fluently
     */
    Application addSharedData(String key, Object data);

    /**
     * Handle event after start all registered {@code Unit} successfully. It will called by {@link
     * #installComponents(Promise)} automatically
     *
     * @param successHandler Success handler after system start component successfully
     */
    void registerSuccessHandler(Handler<Void> successHandler);

    /**
     * Add component provider to startup
     *
     * @param <T>      Type of component
     * @param provider Unit provider
     * @return a reference to this, so the API can be used fluently
     */
    <T extends Component> Application addProvider(ComponentProvider<T> provider);

    /**
     * Add component provider to startup
     *
     * @param <T>            Type of {@code Unit}
     * @param <C>            Type of {@code ComponentContext}
     * @param provider       {@code Unit} provider
     * @param successHandler Success handler after system start {@code Unit} successfully
     * @return a reference to this, so the API can be used fluently
     */
    <C extends ComponentContext, T extends Component> Application addProvider(ComponentProvider<T> provider,
                                                                              Consumer<C> successHandler);

    /**
     * Install a list of register component verticle based on the order of given providers of {@link
     * #addProvider(ComponentProvider)} or {@link #addProvider(ComponentProvider, Consumer)}
     * <p>
     * If any component verticle starts failed, future will catch and report it to {@code Vertx}
     *
     * @param future a future which should be called when all component verticle start-up is complete.
     */
    void installComponents(Promise<Void> future);

    /**
     * Uninstall a list of register components when application is stopped
     *
     * @param future a future which should be called when all component verticle clean-up is complete.
     */
    void uninstallComponents(Promise<Void> future);

}
