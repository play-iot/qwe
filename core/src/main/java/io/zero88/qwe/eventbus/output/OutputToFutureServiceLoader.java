package io.zero88.qwe.eventbus.output;

import java.util.Collection;

import io.vertx.core.ServiceHelper;

import lombok.Getter;

@SuppressWarnings("rawtypes")
public final class OutputToFutureServiceLoader {

    static OutputToFutureServiceLoader instance;

    @Getter
    private final Collection<OutputToFuture> handlers;

    private OutputToFutureServiceLoader() {
        this.handlers = ServiceHelper.loadFactories(OutputToFuture.class, getClass().getClassLoader());
    }

    public static OutputToFutureServiceLoader getInstance() {
        if (instance == null) {
            synchronized (OutputToFutureServiceLoader.class) {
                instance = new OutputToFutureServiceLoader();
            }
        }
        return instance;
    }

}
