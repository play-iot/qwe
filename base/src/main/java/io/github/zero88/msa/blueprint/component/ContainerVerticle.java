package io.github.zero88.msa.blueprint.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import io.github.zero88.msa.blueprint.BlueprintConfig;
import io.github.zero88.msa.blueprint.ConfigProcessor;
import io.github.zero88.msa.blueprint.IConfig;
import io.github.zero88.msa.blueprint.event.EventbusClient;
import io.github.zero88.msa.blueprint.exceptions.BlueprintException;
import io.github.zero88.msa.blueprint.exceptions.converter.BlueprintExceptionConverter;
import io.github.zero88.msa.blueprint.utils.ExecutorHelpers;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;

import lombok.Getter;

/**
 * @see Container
 */
public abstract class ContainerVerticle extends AbstractVerticle implements Container {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Class<? extends Unit>, UnitProvider<? extends Unit>> components = new LinkedHashMap<>();
    private final Map<Class<? extends Unit>, Consumer<? extends UnitContext>> afterSuccesses = new HashMap<>();
    private final Set<String> deployments = new HashSet<>();
    @Getter
    protected BlueprintConfig config;
    @Getter
    private EventbusClient eventbusClient;
    private Handler<Void> successHandler;

    @Override
    public void start() {
        final BlueprintConfig fileConfig = computeConfig(config());
        this.config = new ConfigProcessor(vertx).override(fileConfig.toJson(), true, false).orElse(fileConfig);
        this.eventbusClient = new DefaultEventClient(this.vertx.getDelegate(), this.config.getSystemConfig()
                                                                                          .getEventBusConfig()
                                                                                          .getDeliveryOptions());
        this.registerEventbus(eventbusClient);
        this.addSharedData(SharedDataDelegate.SHARED_EVENTBUS, this.eventbusClient)
            .addSharedData(SharedDataDelegate.SHARED_DATADIR, this.config.getDataDir().toAbsolutePath().toString());
    }

    @Override
    public void start(Future<Void> future) {
        this.start();
        this.installUnits(future);
    }

    @Override
    public void stop(Future<Void> future) {
        this.stopUnits(future);
    }

    @Override
    public void registerEventbus(EventbusClient eventClient) { }

    @Override
    public final Container addSharedData(String key, Object data) {
        this.vertx.sharedData().getLocalMap(getSharedKey()).put(key, data);
        return this;
    }

    public void registerSuccessHandler(Handler<Void> successHandler) {
        this.successHandler = successHandler;
    }

    @Override
    public final <T extends Unit> Container addProvider(UnitProvider<T> provider) {
        this.components.put(provider.unitClass(), provider);
        return this;
    }

    @Override
    public final <C extends UnitContext, T extends Unit> Container addProvider(UnitProvider<T> provider,
                                                                               Consumer<C> successHandler) {
        this.addProvider(provider);
        this.afterSuccesses.put(provider.unitClass(), successHandler);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void installUnits(Future<Void> future) {
        ExecutorHelpers.blocking(getVertx(), components::entrySet).flattenAsObservable(s -> s).flatMapSingle(entry -> {
            Unit unit = entry.getValue().get().registerSharedKey(getSharedKey());
            JsonObject deployConfig = IConfig.from(this.config, unit.configClass()).toJson();
            DeploymentOptions options = new DeploymentOptions().setConfig(deployConfig);
            return vertx.rxDeployVerticle(unit, options)
                        .doOnSuccess(deployId -> succeed(unit, deployId))
                        .doOnError(t -> logger.error("Cannot start unit verticle {}", t, unit.getClass().getName()));
        }).count().map(count -> {
            logger.info("Deployed {} unit verticle(s)...", count);
            Optional.ofNullable(successHandler).ifPresent(handler -> handler.handle(null));
            return count;
        }).subscribe(count -> future.complete(), throwable -> fail(future, throwable));
    }

    @Override
    public final void stopUnits(Future<Void> future) {
        Flowable.fromIterable(this.deployments)
                .parallel()
                .map(vertx::rxUndeploy)
                .reduce(Completable::mergeWith)
                .count()
                .subscribe(c -> {
                    logger.info("Uninstall {} verticle successfully", c);
                    future.complete();
                }, future::fail);
    }

    private void fail(Future<Void> future, Throwable throwable) {
        BlueprintException t = BlueprintExceptionConverter.from(throwable);
        logger.error("Cannot start container verticle {}", t, this.getClass().getName());
        future.fail(t);
    }

    @SuppressWarnings("unchecked")
    private void succeed(Unit unit, String deployId) {
        logger.info("Deployed Verticle '{}' successful with ID '{}'", unit.getClass().getName(), deployId);
        deployments.add(deployId);
        Consumer<UnitContext> consumer = (Consumer<UnitContext>) this.afterSuccesses.get(unit.getClass());
        if (Objects.nonNull(consumer)) {
            consumer.accept(unit.getContext().registerDeployId(deployId));
        }
    }

}
