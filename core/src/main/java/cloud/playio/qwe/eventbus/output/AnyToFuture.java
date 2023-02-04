package cloud.playio.qwe.eventbus.output;

import io.vertx.core.Future;
import cloud.playio.qwe.eventbus.refl.MethodMeta;

public final class AnyToFuture implements OutputToFuture<Object> {

    @Override
    public Class<Object> outputClass() {
        return Object.class;
    }

    @Override
    public boolean verify(MethodMeta methodMeta) {
        return true;
    }

    @Override
    public Future<Object> transform(MethodMeta methodMeta, Object response) {
        return Future.succeededFuture(response);
    }

}
