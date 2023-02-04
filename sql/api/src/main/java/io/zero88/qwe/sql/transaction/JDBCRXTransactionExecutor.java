package cloud.playio.qwe.sql.transaction;

import org.jooq.DSLContext;
import org.jooq.TransactionalCallable;

import io.vertx.core.Future;

import lombok.NonNull;

public interface JDBCRXTransactionExecutor {

    static JDBCRXTransactionExecutor create(@NonNull DSLContext context) {
        return new JDBCRXTransactionExecutorImpl(context);
    }

    @NonNull DSLContext dsl();

    @NonNull <T> Future<T> transactionResult(TransactionalCallable<Future<T>> transactional);

}
