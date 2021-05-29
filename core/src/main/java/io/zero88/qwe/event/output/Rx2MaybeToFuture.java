package io.zero88.qwe.event.output;

import io.reactivex.Maybe;
import io.vertx.core.Future;
import io.vertx.reactivex.MaybeHelper;
import io.zero88.qwe.event.refl.MethodMeta;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class Rx2MaybeToFuture implements OutputToFuture<Maybe> {

    @Override
    public Class<Maybe> outputClass() {
        return Maybe.class;
    }

    @Override
    public Future<Object> transform(MethodMeta methodMeta, Object response) {
        return MaybeHelper.toFuture((Maybe<Object>) response);
    }

}
