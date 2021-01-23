package io.github.zero88.qwe.component;

import io.github.zero88.qwe.CarlConfig;
import io.github.zero88.qwe.event.EventListener;
import io.github.zero88.qwe.event.EventModel;
import io.github.zero88.qwe.event.EventbusClient;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;

import lombok.NonNull;

/**
 * Represents a container consists a list of {@code Verticle component} to startup application
 *
 * @see Component
 * @see HasConfig
 * @see ApplicationVerticle
 */
public interface Application extends HasConfig<CarlConfig>, HasSharedKey, HasSharedData, Verticle {

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
     * Add component provider to startup
     *
     * @param <T>      Type of component
     * @param provider Unit provider
     * @return a reference to this, so the API can be used fluently
     */
    <T extends Component> Application addProvider(ComponentProvider<T> provider);

    /**
     * Install a list of register component verticle based on the order of given providers of {@link
     * #addProvider(ComponentProvider)}
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

    /**
     * Raise event after installed all component completely
     *
     * @param lookup Context lookup
     */
    void onInstallCompleted(@NonNull ContextLookup lookup);

}
