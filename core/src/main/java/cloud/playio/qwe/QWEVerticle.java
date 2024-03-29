package cloud.playio.qwe;

import java.util.function.Supplier;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import cloud.playio.qwe.exceptions.QWEExceptionConverter;

/**
 * Represents for Vertx verticle in QWE architecture
 *
 * @param <C> Type of IConfig
 * @see HasSharedData
 * @see Verticle
 */
interface QWEVerticle<C extends IConfig> extends HasConfig<C>, HasSharedData, Verticle {

    static void asyncRun(Vertx vertx, Promise<Void> onCompleteHandler, Runnable beforeOnAsyncAction,
                         Supplier<Future<Void>> asyncActionProvider) {
        vertx.executeBlocking(p -> {
            try {
                beforeOnAsyncAction.run();
                p.complete();
            } catch (Exception ex) {
                p.fail(QWEExceptionConverter.friendlyOrKeep(ex));
            }
        }).flatMap(ignore -> asyncActionProvider.get()).onComplete(onCompleteHandler);
    }

}
