package cloud.playio.qwe.micro;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.HasSharedData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.dto.msg.ResponseData;

@VertxGen
public interface ServiceDiscoveryApi extends Supplier<ServiceDiscovery>, HasSharedData, HasLogger {

    /**
     * Register new record
     *
     * @param record service record
     * @return record future
     */
    Future<Record> register(Record record);

    /**
     * Register one or many records
     *
     * @param records service records
     * @return a list record future
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<List<Record>> register(Record... records) {
        return register(Arrays.asList(records));
    }

    /**
     * Register list of records
     *
     * @param records service records
     * @return a list record future
     */
    Future<List<Record>> register(List<Record> records);

    /**
     * Update record by registration
     *
     * @param record record
     * @return a record future
     */
    Future<Record> update(Record record);

    /**
     * Batch update records
     *
     * @param filter     request filter
     * @param updateData a update data
     * @return a list record future
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<List<Record>> batchUpdate(RequestFilter filter, JsonObject updateData);

    /**
     * Unregister records then return result in json format with keys: {@code total}, {@code removed}, {@code filter},
     * {@code errors}
     *
     * @param filter request filter
     * @return a unregistered async result
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<JsonObject> unregister(RequestFilter filter);

    /**
     * Find one record
     *
     * @param filter request filter
     * @return a future record or failed future if not found or many records corresponding to given filter
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Record> findOne(RequestFilter filter);

    /**
     * Find many records
     *
     * @param filter request filter
     * @return a list record future
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<List<Record>> findMany(RequestFilter filter);

    /**
     * Remote execute service with request data after found one service record by request filter
     *
     * @param filter      request filter
     * @param requestData request data to execute
     * @return a response data future
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<ResponseData> execute(RequestFilter filter, RequestData requestData);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Logger logger() {
        return LogManager.getLogger(ServiceDiscoveryApi.class);
    }

}
