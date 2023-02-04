package cloud.playio.qwe.eventbus.output;

import io.vertx.core.Future;
import cloud.playio.qwe.eventbus.refl.MethodMeta;

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
