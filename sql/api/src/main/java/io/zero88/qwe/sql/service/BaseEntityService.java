package io.zero88.qwe.sql.service;

import io.zero88.qwe.sql.EntityMetadata;
import io.zero88.qwe.sql.handler.EntityHandler;

import lombok.NonNull;

interface BaseEntityService<M extends EntityMetadata> {

    /**
     * Declares entity handler
     *
     * @return entity handler
     * @see EntityHandler
     * @since 1.0.0
     */
    @NonNull EntityHandler entityHandler();

    /**
     * Declares physical database entity metadata
     *
     * @return entity metadata
     * @see EntityMetadata
     * @since 1.0.0
     */
    @NonNull M context();

}
