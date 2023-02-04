package cloud.playio.qwe.sql.service;

import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.query.SimpleQueryExecutor;

import lombok.NonNull;

interface SimpleEntityService<P extends JsonRecord, M extends EntityMetadata> extends EntityService<P, M> {

    @Override
    @NonNull SimpleQueryExecutor<P> queryExecutor();

}
