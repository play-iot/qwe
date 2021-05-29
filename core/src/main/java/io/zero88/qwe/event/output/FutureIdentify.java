package io.zero88.qwe.event.output;

import io.vertx.core.Future;
import io.zero88.qwe.event.refl.MethodMeta;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class FutureIdentify implements OutputToFuture<Future> {

    @Override
    public Class<Future> outputClass() {
        return Future.class;
    }

    @Override
    public Future<Object> transform(MethodMeta methodMeta, Object response) {
        return (Future<Object>) response;
    }

}
