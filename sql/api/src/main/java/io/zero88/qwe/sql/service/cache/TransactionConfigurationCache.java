package io.zero88.qwe.sql.service.cache;

import org.jooq.Configuration;

import io.zero88.qwe.cache.AbstractLocalCache;
import io.zero88.qwe.micro.GatewayHeaders;

public final class TransactionConfigurationCache
    extends AbstractLocalCache<String, Configuration, TransactionConfigurationCache> {

    @Override
    protected String keyLabel() {
        return GatewayHeaders.X_CORRELATION_ID;
    }

    @Override
    protected String valueLabel() {
        return "Jooq Configuration";
    }

}
