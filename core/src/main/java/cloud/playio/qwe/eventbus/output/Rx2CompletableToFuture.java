package cloud.playio.qwe.eventbus.output;

import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.reactivex.CompletableHelper;
import cloud.playio.qwe.eventbus.refl.MethodMeta;

public final class Rx2CompletableToFuture implements OutputToFuture<Completable> {

    @Override
    public Class<Completable> outputClass() {
        return Completable.class;
    }

    @Override
    public Future<Object> transform(MethodMeta methodMeta, Object response) {
        return CompletableHelper.toFuture((Completable) response).mapEmpty();
    }

}
