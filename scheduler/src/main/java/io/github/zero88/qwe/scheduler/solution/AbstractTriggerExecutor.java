package io.github.zero88.qwe.scheduler.solution;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractTriggerExecutor<T extends Trigger> implements TriggerExecutor<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NonNull
    private final T trigger;

    @Override
    public T get() {
        return trigger;
    }

}
