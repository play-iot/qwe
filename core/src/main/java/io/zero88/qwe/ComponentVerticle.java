package io.zero88.qwe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ComponentVerticle<C extends IConfig, T extends ComponentContext> extends AbstractVerticle
    implements Component<C, T>, DeployHook<T>, VerticleLifecycleHooks {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Getter
    @NonNull
    @Accessors(fluent = true)
    private final SharedDataLocalProxy sharedData;
    private T componentContext;
    protected C config;

    @Override
    public final void start() {
        logger.debug("Start Component [{}]...", appName());
        this.config = computeConfig(logger, config());
        this.onStart();
    }

    @Override
    public final void start(Promise<Void> promise) {
        QWEVerticle.asyncRun(vertx, promise, this::start, this::onAsyncStart);
    }

    @Override
    public final void stop() {
        logger.debug("Stop Component [{}]...", appName());
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

    @Override
    public final T getContext() {
        return componentContext;
    }

}
