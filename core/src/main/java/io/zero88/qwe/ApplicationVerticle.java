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
import io.zero88.qwe.exceptions.QWEExceptionConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @see Application
 */
@SuppressWarnings("rawtypes")
public abstract class ApplicationVerticle extends AbstractVerticle
    implements Application, SharedDataLocalProxy, VerticleLifecycleHooks {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Class<? extends Component>, ComponentProvider<? extends Component>> providers = new HashMap<>();
    private final ContextLookupInternal contexts = new ContextLookupImpl();
    @Getter
    @Accessors(fluent = true)
    protected QWEAppConfig appConfig;
    @Getter
    private EventBusClient eventBus;

    @Override
    public final void start() {
        logger.info("Start Application[{}]...", appName());
        this.appConfig = computeConfig(logger, config());
        this.addData(SharedDataLocalProxy.EVENTBUS_DELIVERY_OPTION,
                     new EventBusDeliveryOption(this.appConfig.getDeliveryOptions()));
        this.addData(SharedDataLocalProxy.APP_DATADIR, this.appConfig.getDataDir());
        this.eventBus = EventBusClient.create(sharedData());
        this.onStart();
    }

    @Override
    public final void start(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::start, () -> onAsyncStart().flatMap(ignore -> installComponents())
                                                                              .onFailure(e -> logger.error(
                                                                                  "Failed to start Application[{}]",
                                                                                  appName(), e))
                                                                              .mapEmpty());
    }

    public final void stop() {
        logger.info("Stop Application[{}]...", appName());
        this.onStop();
    }

    @Override
    public final void stop(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::stop, () -> onAsyncStop().eventually(ignore -> uninstallComponents())
                                                                            .onFailure(e -> logger.error(
                                                                                "Failed to stop Application[{}]",
                                                                                appName(), e)));
    }

    @Override
    public final <T extends Component> Application addProvider(ComponentProvider<T> provider) {
        this.providers.put(provider.componentClass(), provider);
        return this;
    }

    @Override
    public final SharedDataLocalProxy sharedData() {
        return this;
    }

    @Override
    public final Future<Void> installComponents() {
        return CompositeFuture.all(providers.values().stream().map(this::deployComponent).collect(Collectors.toList()))
                              .onSuccess(this::succeed)
                              .mapEmpty();
    }

    @Override
    public final Future<Void> uninstallComponents() {
        return CompositeFuture.join(
            this.contexts.list().stream().map(this::uninstallComponent).collect(Collectors.toList()))
                              .onSuccess(ar -> logger.info("Uninstall {} component(s) successfully", ar.size()))
                              .mapEmpty();
    }

    @Override
    public void onInstallCompleted(ContextLookup lookup) { }

    private Future<Void> uninstallComponent(ComponentContext context) {
        return vertx.undeploy(context.deployId())
                    .onSuccess(unused -> logger.info("Component [{}][{}] is uninstalled", context.deployId(),
                                                     context.appName()));
    }

    @SuppressWarnings("unchecked")
    private Future<String> deployComponent(ComponentProvider<? extends Component> provider) {
        final Component component = provider.provide(this);
        final JsonObject deployConfig = IConfig.from(this.appConfig, component.configClass()).toJson();
        logger.info("Deploying Component [{}]...", component.appName());
        return vertx.deployVerticle(component, new DeploymentOptions().setConfig(deployConfig))
                    .onSuccess(id -> deployComponentSuccess(component, id))
                    .recover(t -> Future.failedFuture(QWEExceptionConverter.friendlyOrKeep(t)));
    }

    @SuppressWarnings("unchecked")
    private void deployComponentSuccess(Component component, String deployId) {
        logger.info("Succeed deploying Component [{}][{}]", component.appName(), deployId);
        ComponentContext def = ComponentContext.create(component.appName(), appConfig.dataDir(), getSharedKey(),
                                                       deployId);
        contexts.add(component.setup(component.hook().onSuccess(def)));
    }

    private void succeed(CompositeFuture r) {
        logger.info("Deployed {}/{} Component(s)...", r.size(), this.providers.size());
        this.providers.clear();
        try {
            //TODO: add liveness event then readiness event
            this.onInstallCompleted(this.contexts);
        } catch (Exception e) {
            //TODO: add failure readiness event
            logger.warn("Failed after completed Component deployment", QWEExceptionConverter.friendlyOrKeep(e));
        }
    }

}
