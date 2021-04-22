package io.zero88.qwe.component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.CarlConfig;
import io.zero88.qwe.ConfigProcessor;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.event.EventbusClient;
import io.zero88.qwe.event.EventbusDeliveryOption;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.converter.CarlExceptionConverter;

import lombok.Getter;

/**
 * @see Application
 */
@SuppressWarnings("rawtypes")
public abstract class ApplicationVerticle extends AbstractVerticle implements Application, SharedDataLocalProxy {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Class<? extends Component>, ComponentProvider<? extends Component>> providers = new HashMap<>();
    private final ContextLookupInternal contexts = new ContextLookupImpl();
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
        this.eventbus = EventbusClient.create(this.vertx, option.get());
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
    public SharedDataLocalProxy sharedData() {
        return this;
    }

    @Override
    public final void installComponents(Promise<Void> promise) {
        CompositeFuture.join(providers.values().stream().map(this::deployComponent).collect(Collectors.toList()))
                       .onFailure(t -> fail(promise, t))
                       .onSuccess(r -> succeed(promise, r));
    }

    @Override
    public final void uninstallComponents(Promise<Void> future) {
        CompositeFuture.join(this.contexts.list().stream().map(this::uninstallComponent).collect(Collectors.toList()))
                       .onSuccess(ar -> {
                           logger.info("Uninstall {} component(s) successfully", ar.size());
                           future.complete();
                       })
                       .onFailure(future::fail);
    }

    @Override
    public void onInstallCompleted(ContextLookup lookup) { }

    private Future<Void> uninstallComponent(ComponentContext context) {
        return vertx.undeploy(context.deployId())
                    .onSuccess(unused -> logger.info("Component '{}' is uninstalled", context.componentClz()));
    }

    @SuppressWarnings("unchecked")
    private Future<String> deployComponent(ComponentProvider<? extends Component> provider) {
        final Component component = provider.provide(this);
        final JsonObject deployConfig = IConfig.from(this.config, component.configClass()).toJson();
        return vertx.deployVerticle(component, new DeploymentOptions().setConfig(deployConfig))
                    .onFailure(t -> deployComponentError(component, t))
                    .onSuccess(id -> deployComponentSuccess(component, id));
    }

    private void deployComponentError(Component component, Throwable t) {
        logger.error("Cannot start component verticle {}", component.getClass().getName(), t);
        component.hook().onError(t);
    }

    @SuppressWarnings("unchecked")
    private void deployComponentSuccess(Component component, String deployId) {
        final Class<? extends Component> clazz = component.getClass();
        logger.info("Deployed Verticle '{}' successful with ID '{}'", clazz.getName(), deployId);
        final ComponentContext def = ComponentContext.create(clazz, config.getDataDir(), getSharedKey(), deployId);
        contexts.add(component.setup(component.hook().onSuccess(def)));
    }

    private void succeed(Promise<Void> promise, CompositeFuture r) {
        logger.info("Deployed {}/{} component verticle(s)...", r.size(), this.providers.size());
        this.providers.clear();
        this.onInstallCompleted(this.contexts);
        promise.tryComplete();
    }

    private void fail(Promise<Void> promise, Throwable throwable) {
        CarlException t = CarlExceptionConverter.from(throwable);
        logger.error("Cannot start container verticle {}", this.getClass().getName(), t);
        promise.fail(t);
    }

}
