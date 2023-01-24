package io.zero88.qwe.sql.service;

import io.github.zero88.jooqx.JsonRecord;
import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.query.SimpleQueryExecutor;

import lombok.NonNull;

interface SimpleEntityService<P extends JsonRecord, M extends EntityMetadata> extends EntityService<P, M> {

    @Override
    @NonNull SimpleQueryExecutor<P> queryExecutor();

}
