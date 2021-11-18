package io.zero88.qwe.sql.transaction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.TransactionContext;
import org.jooq.TransactionListener;
import org.jooq.TransactionListenerProvider;
import org.jooq.TransactionProvider;
import org.jooq.TransactionalCallable;
import org.jooq.exception.DataAccessException;

import io.github.zero88.utils.Functions;
import io.vertx.core.Future;
import io.zero88.qwe.sql.exceptions.TransactionalException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class JDBCRXTransactionExecutorImpl implements JDBCRXTransactionExecutor {

    private final DSLContext dsl;

    @Override
    public @NonNull <T> Future<T> transactionResult(TransactionalCallable<Future<T>> transactional) {
        return transactionResult0(transactional, dsl().configuration());
    }

    <T> Future<T> transactionResult0(TransactionalCallable<Future<T>> transactional, Configuration configuration) {
        final TransactionContext context = new JDBCRXTransactionContext(configuration.derive());
        final TransactionProvider provider = context.configuration().transactionProvider();
        final TransactionListener listeners = new TransactionListenerContainer(
            Arrays.stream(context.configuration().transactionListenerProviders())
                  .map(TransactionListenerProvider::provide)
                  .collect(Collectors.toList()));
        return Future.succeededFuture(context)
                     .map(c -> beginTransaction(c, provider, listeners))
                     .flatMap(ctx -> Functions.getOrThrow(TransactionalException::new,
                                                          () -> transactional.run(ctx.configuration())))
                     .map(r -> {
                         commitTransaction(context, provider, listeners);
                         return r;
                     })
                     .recover(cause -> Future.failedFuture(handleRollback(context, provider, listeners, cause)));
    }

    private void commitTransaction(TransactionContext ctx, TransactionProvider provider,
                                   TransactionListener listeners) {
        try {
            listeners.commitStart(ctx);
            provider.commit(ctx);
        } finally {
            listeners.commitEnd(ctx);
        }
    }

    private TransactionContext beginTransaction(TransactionContext ctx, TransactionProvider provider,
                                                TransactionListener listeners) {
        try {
            listeners.beginStart(ctx);
            provider.begin(ctx);
            return ctx;
        } finally {
            listeners.beginEnd(ctx);
        }
    }

    private RuntimeException handleRollback(@NonNull TransactionContext ctx, @NonNull TransactionProvider provider,
                                            @NonNull TransactionListener listeners, @NonNull Throwable throwable) {
        Throwable cause = throwable instanceof TransactionalException ? throwable.getCause() : throwable;

        // [#6608] [#7167] Errors are no longer handled differently
        if (cause instanceof Exception) {
            ctx.cause((Exception) cause);
        } else {
            ctx.causeThrowable(cause);
        }

        listeners.rollbackStart(ctx);
        try {
            provider.rollback(ctx);
        } catch (Exception suppress) {
            // [#3718] Use reflection to support also JDBC 4.0
            cause.addSuppressed(suppress);
        }
        listeners.rollbackEnd(ctx);

        // [#6608] [#7167] Errors are no longer handled differently
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw new RuntimeException(cause);
        } else {
            throw new DataAccessException("Rollback caused", cause);
        }
    }

    @RequiredArgsConstructor
    private static final class TransactionListenerContainer implements TransactionListener {

        @NonNull
        private final List<TransactionListener> listeners;

        @Override
        public void beginStart(TransactionContext ctx) {
            listeners.forEach(l -> l.beginStart(ctx));
        }

        @Override
        public void beginEnd(TransactionContext ctx) {
            listeners.forEach(l -> l.beginEnd(ctx));
        }

        @Override
        public void commitStart(TransactionContext ctx) {
            listeners.forEach(l -> l.commitStart(ctx));
        }

        @Override
        public void commitEnd(TransactionContext ctx) {
            listeners.forEach(l -> l.commitEnd(ctx));
        }

        @Override
        public void rollbackStart(TransactionContext ctx) {
            listeners.forEach(l -> l.rollbackStart(ctx));
        }

        @Override
        public void rollbackEnd(TransactionContext ctx) {
            listeners.forEach(l -> l.rollbackEnd(ctx));
        }

    }

}
