package io.zero88.qwe.eventbus.output;

import java.util.Collection;

import io.vertx.core.ServiceHelper;

import lombok.Getter;

@SuppressWarnings("rawtypes")
public final class OutputToFutureServiceLoader {

    @Getter
    private final Collection<OutputToFuture> handlers;

    public OutputToFutureServiceLoader() {
        this.handlers = ServiceHelper.loadFactories(OutputToFuture.class, getClass().getClassLoader());
    }

}
