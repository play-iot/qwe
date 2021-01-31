package io.github.zero88.qwe.component;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.CarlConfig;
import io.github.zero88.qwe.ConfigProcessor;
import io.github.zero88.qwe.IConfig;
import io.github.zero88.qwe.event.EventbusClient;
import io.github.zero88.qwe.event.EventbusDeliveryOption;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.converter.CarlExceptionConverter;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

import lombok.Getter;

/**
 * @see Application
 */
@SuppressWarnings("rawtypes")
public abstract class ApplicationVerticle extends AbstractVerticle implements Application, SharedDataLocalProxy {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Class<? extends Component>, ComponentProvider<? extends Component>> providers = new HashMap<>();
    private final ContextLookup contexts = new ContextLookupImpl();
    @Getter
    protected CarlConfig config;
    @Getter
    private EventbusClient eventbus;

    @Override
    public void start() {
        final CarlConfig fileConfig = computeConfig(config());
        this.config = new ConfigProcessor(vertx).override(fileConfig.toJson(), true, false).orElse(fileConfig);
        final EventbusDeliveryOption option = new EventbusDeliveryOption(
            this.config.getSystemConfig().getEventBusConfig().getDeliveryOptions());
        this.eventbus = EventbusClient.create(this.vertx.getDelegate(), option.get());
        this.registerEventbus(eventbus);
        this.addData(SharedDataLocalProxy.EVENTBUS_OPTION, option);
        this.addData(SharedDataLocalProxy.APP_DATADIR, this.config.getDataDir().toAbsolutePath().toString());
    }

    @Override
    public void start(Promise<Void> promise) {
        this.start();
        this.installComponents(promise);
    }

    @Override
    public void stop(Promise<Void> promise) {
        this.uninstallComponents(promise);
    }

    @Override
    public void registerEventbus(EventbusClient eventClient) { }

    @Override
    public final <T extends Component> Application addProvider(ComponentProvider<T> provider) {
        this.providers.put(provider.componentClass(), provider);
        return this;
    }

    @Override
    public final void installComponents(Promise<Void> promise) {
        Observable.fromIterable(providers.values())
                  .flatMapSingle(this::deployComponent)
                  .count()
                  .doOnSuccess(this::succeed)
                  .subscribe(count -> promise.complete(), throwable -> fail(promise, throwable));
    }

    @Override
    public void onInstallCompleted(ContextLookup lookup) { }

    @Override
    public SharedDataLocalProxy sharedData() {
        return this;
    }

    @Override
    public final void uninstallComponents(Promise<Void> future) {
        Flowable.fromIterable(((ContextLookupImpl) this.contexts).values())
                .parallel()
                .map(this::uninstallComponent)
                .reduce(Completable::mergeWith)
                .count()
                .doOnSuccess(c -> logger.info("Uninstall {} component(s) successfully", c))
                .subscribe(c -> future.complete(), future::fail);
    }

    private Completable uninstallComponent(ComponentContext context) {
        return vertx.rxUndeploy(context.deployId())
                    .doOnComplete(() -> logger.info("Component '{}' is uninstalled", context.componentClz()));
    }

    @SuppressWarnings("unchecked")
    private Single<String> deployComponent(ComponentProvider<? extends Component> provider) {
        final Component component = provider.provide(this);
        final JsonObject deployConfig = IConfig.from(this.config, component.configClass()).toJson();
        return vertx.rxDeployVerticle(component, new DeploymentOptions().setConfig(deployConfig))
                    .doOnSuccess(deployId -> deployComponentSuccess(component, deployId))
                    .doOnError(t -> deployComponentError(component, t));
    }

    private void deployComponentError(Component component, Throwable t) {
        logger.error("Cannot start component verticle {}", component.getClass().getName(), t);
        component.hook().onError(t);
    }

    private void deployComponentSuccess(Component component, String deployId) {
        final Class<? extends Component> clazz = component.getClass();
        logger.info("Deployed Verticle '{}' successful with ID '{}'", clazz.getName(), deployId);
        final ComponentContext def = ComponentContext.create(clazz, config.getDataDir(), getSharedKey(), deployId);
        @SuppressWarnings("unchecked")
        final ComponentContext ctx = component.setup(component.hook().onSuccess(def));
        ((ContextLookupImpl) this.contexts).put(ctx.getClass(), ctx);
    }

    private void succeed(Long count) {
        logger.info("Deployed {}/{} component verticle(s)...", count, this.providers.size());
        this.providers.clear();
        this.onInstallCompleted(this.contexts);
    }

    private void fail(Promise<Void> promise, Throwable throwable) {
        CarlException t = CarlExceptionConverter.from(throwable);
        logger.error("Cannot start container verticle {}", this.getClass().getName(), t);
        promise.fail(t);
    }

}
