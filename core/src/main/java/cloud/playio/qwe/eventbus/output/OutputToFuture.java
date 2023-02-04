package cloud.playio.qwe.eventbus.output;

import io.github.zero88.repl.ReflectionClass;
import io.vertx.core.Future;
import cloud.playio.qwe.eventbus.refl.MethodMeta;

/**
 * Cast or wrap a {@code response} in any data type into a {@code Future}
 *
 * @param <T> output type
 */
public interface OutputToFuture<T> {

    Class<T> outputClass();

    default boolean verify(MethodMeta methodMeta) {
        return ReflectionClass.assertDataType(methodMeta.method().getReturnType(), outputClass());
    }

    Future<Object> transform(MethodMeta methodMeta, Object response);

}
