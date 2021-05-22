package io.zero88.qwe;

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
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventBusDeliveryOption;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.QWEExceptionConverter;

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
    protected QWEConfig config;
    @Getter
    private EventBusClient eventBus;

    @Override
    public void start() {
        final QWEConfig fileConfig = computeConfig(config());
        this.config = new ConfigProcessor(vertx).override(fileConfig.toJson(), true, false).orElse(fileConfig);
        this.addData(SharedDataLocalProxy.EVENTBUS_DELIVERY_OPTION, new EventBusDeliveryOption(
            this.config.getSystemConfig().getEventBusConfig().getDeliveryOptions()));
        this.addData(SharedDataLocalProxy.APP_DATADIR, this.config.getDataDir().toAbsolutePath().toString());
        this.eventBus = EventBusClient.create(sharedData());
        this.registerEventBus(eventBus);
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
    public void registerEventBus(EventBusClient eventBus) { }

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
        final ComponentContext def = ComponentContext.create(clazz, config.dataDir(), getSharedKey(), deployId);
        contexts.add(component.setup(component.hook().onSuccess(def)));
    }

    private void succeed(Promise<Void> promise, CompositeFuture r) {
        logger.info("Deployed {}/{} component verticle(s)...", r.size(), this.providers.size());
        this.providers.clear();
        try {
            this.onInstallCompleted(this.contexts);
            promise.tryComplete();
        } catch (Exception e) {
            promise.fail(e);
        }
    }

    private void fail(Promise<Void> promise, Throwable throwable) {
        QWEException t = QWEExceptionConverter.from(throwable);
        logger.error("Cannot start container verticle {}", this.getClass().getName(), t);
        promise.fail(t);
    }

}
