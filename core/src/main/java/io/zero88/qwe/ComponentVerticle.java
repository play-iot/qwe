package io.zero88.qwe;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ComponentVerticle<C extends ComponentConfig, T extends ComponentContext> extends AbstractVerticle
    implements Component<C, T>, DeployHook<T>, VerticleLifecycleHooks {

    @Getter
    @NonNull
    private final SharedDataLocalProxy sharedData;
    @Getter
    protected C componentConfig;
    @Getter
    private T componentContext;

    @Override
    public final void start() {
        logger().debug("Start Component [{}]...", appName());
        this.componentConfig = computeConfig(config());
        this.onStart();
    }

    @Override
    public final void start(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::start, this::onAsyncStart);
    }

    @Override
    public final void stop() {
        logger().debug("Stop Component [{}]...", appName());
        this.onStop();
    }

    @Override
    public final void stop(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::stop, this::onAsyncStop);
    }

    @Override
    public @NonNull DeployHook<T> hook() {
        return this;
    }

    @Override
    public final T setup(T context) {
        return this.componentContext = context;
    }

}
