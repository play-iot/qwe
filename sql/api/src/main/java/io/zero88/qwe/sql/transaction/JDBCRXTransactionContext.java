package cloud.playio.qwe.sql.transaction;

import java.util.HashMap;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Transaction;
import org.jooq.TransactionContext;
import org.jooq.conf.Settings;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
final class JDBCRXTransactionContext implements TransactionContext {

    @Getter
    @NonNull
    private final Configuration configuration;
    private final Map<Object, Object> data = new HashMap<>();
    Transaction transaction;
    Throwable cause;

    @Override
    public final Transaction transaction() {
        return transaction;
    }

    @Override
    public final TransactionContext transaction(Transaction t) {
        transaction = t;
        return this;
    }

    @Override
    public final Exception cause() {
        return cause instanceof Exception ? (Exception) cause : null;
    }

    @Override
    public final Throwable causeThrowable() {
        return cause;
    }

    @Override
    public final TransactionContext cause(Exception c) {
        cause = c;
        return this;
    }

    @Override
    public final TransactionContext causeThrowable(Throwable c) {
        cause = c;
        return this;
    }

    @Override
    public DSLContext dsl() {
        return configuration.dsl();
    }

    @Override
    public Settings settings() {
        return configuration.settings();
    }

    @Override
    public SQLDialect dialect() {
        return configuration.dialect();
    }

    @Override
    public SQLDialect family() {
        return configuration.family();
    }

    @Override
    public Map<Object, Object> data() {
        return data;
    }

    @Override
    public Object data(Object key) {
        return data.get(key);
    }

    @Override
    public Object data(Object key, Object value) {
        return data.put(key, value);
    }

}
