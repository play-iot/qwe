package cloud.playio.qwe.sql.service.cache;

import org.jooq.Configuration;

import cloud.playio.qwe.cache.AbstractLocalCache;
import cloud.playio.qwe.micro.GatewayHeaders;

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
