package io.github.zero88.msa.bp.component;

import java.util.function.Consumer;

import io.github.zero88.msa.bp.BlueprintConfig;
import io.github.zero88.msa.bp.event.EventListener;
import io.github.zero88.msa.bp.event.EventModel;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents a container consists a list of {@code Verticle unit} to startup application
 *
 * @see Unit
 * @see HasConfig
 * @see ContainerVerticle
 */
public interface Container extends HasConfig<BlueprintConfig> {

    @Override
    default Class<BlueprintConfig> configClass() {
        return BlueprintConfig.class;
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
    Container addSharedData(String key, Object data);

    /**
     * Handle event after start all registered {@code Unit} successfully. It will called by {@link
     * #installUnits(Future)} automatically
     *
     * @param successHandler Success handler after system start component successfully
     */
    void registerSuccessHandler(Handler<Void> successHandler);

    /**
     * Add unit provider to startup
     *
     * @param <T>      Type of unit
     * @param provider Unit provider
     * @return a reference to this, so the API can be used fluently
     */
    <T extends Unit> Container addProvider(UnitProvider<T> provider);

    /**
     * Add unit provider to startup
     *
     * @param <T>            Type of {@code Unit}
     * @param <C>            Type of {@code UnitContext}
     * @param provider       {@code Unit} provider
     * @param successHandler Success handler after system start {@code Unit} successfully
     * @return a reference to this, so the API can be used fluently
     */
    <C extends UnitContext, T extends Unit> Container addProvider(UnitProvider<T> provider, Consumer<C> successHandler);

    /**
     * Install a list of register unit verticle based on the order of given providers of {@link
     * #addProvider(UnitProvider)} or {@link #addProvider(UnitProvider, Consumer)}
     * <p>
     * If any unit verticle starts failed, future will catch and report it to {@code Vertx}
     *
     * @param future a future which should be called when all unit verticle start-up is complete.
     */
    void installUnits(Future<Void> future);

    /**
     * Stop a list of register units
     *
     * @param future a future which should be called when all unit verticle clean-up is complete.
     */
    void stopUnits(Future<Void> future);

}
