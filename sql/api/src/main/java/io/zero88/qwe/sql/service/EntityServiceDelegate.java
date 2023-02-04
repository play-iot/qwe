package cloud.playio.qwe.sql.service;

import java.util.Collection;

import org.slf4j.Logger;

import io.github.zero88.qwe.event.EventMessage;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.github.zero88.jooqx.JsonRecord;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.sql.EntityMetadata;
import cloud.playio.qwe.sql.handler.EntityHandler;
import cloud.playio.qwe.sql.query.EntityQueryExecutor;
import cloud.playio.qwe.sql.service.decorator.RequestDecorator;
import cloud.playio.qwe.sql.service.transformer.EntityTransformer;
import cloud.playio.qwe.sql.validation.EntityValidation;
import cloud.playio.qwe.sql.workflow.task.EntityTaskManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

/**
 * The interface Entity service delegate.
 *
 * @param <P> Type of {@code JsonRecord}
 * @param <M> Type of {@code Entity Metadata}
 * @param <S> Type of {@code Entity Service}
 * @since 1.0.0
 */
public interface EntityServiceDelegate<P extends JsonRecord, M extends EntityMetadata, S extends EntityService<P, M>>
    extends EntityService<P, M> {

    /**
     * Unwrap Entity service.
     *
     * @return the service
     * @since 1.0.0
     */
    S unwrap();

    @Override
    default @NonNull EntityHandler entityHandler() {
        return unwrap().entityHandler();
    }

    @Override
    default M context() {
        return unwrap().context();
    }

    @Override
    default @NonNull Collection<EventAction> getAvailableEvents() {
        return unwrap().getAvailableEvents();
    }

    @Override
    default @NonNull EntityQueryExecutor<P> queryExecutor() {
        return unwrap().queryExecutor();
    }

    @Override
    default @NonNull RequestDecorator requestDecorator() {
        return unwrap().requestDecorator();
    }

    @Override
    default EntityValidation validation() {
        return unwrap().validation();
    }

    @Override
    default @NonNull EntityTransformer transformer() {
        return unwrap().transformer();
    }

    @Override
    default @NonNull EntityTaskManager taskManager() {
        return unwrap().taskManager();
    }

    @Override
    default boolean supportForceDeletion() {
        return unwrap().supportForceDeletion();
    }

    @Override
    default Future<JsonObject> list(RequestData requestData) {
        return unwrap().list(requestData);
    }

    @Override
    default Future<JsonObject> get(RequestData requestData) {
        return unwrap().get(requestData);
    }

    @Override
    default Future<JsonObject> create(RequestData requestData) {
        return unwrap().create(requestData);
    }

    @Override
    default Future<JsonObject> update(RequestData requestData) {
        return unwrap().update(requestData);
    }

    @Override
    default Future<JsonObject> patch(RequestData requestData) {
        return unwrap().patch(requestData);
    }

    @Override
    default Future<JsonObject> delete(RequestData requestData) {
        return unwrap().delete(requestData);
    }

    @Override
    default Logger logger() {
        return unwrap().logger();
    }

    @Override
    default ObjectMapper mapper() {
        return unwrap().mapper();
    }

    @Override
    default String fallback() {
        return unwrap().fallback();
    }

    @Override
    default Single<EventMessage> apply(Message<Object> message) {
        return unwrap().apply(message);
    }

}
