package io.zero88.qwe.rpc;

import java.util.Optional;
import java.util.function.Function;

import io.github.zero88.utils.Strings;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Status;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.converter.ErrorMessageConverter;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventPattern;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.micro.ServiceNotFoundException;
import io.zero88.qwe.micro.filter.ByPredicate;
import io.zero88.qwe.micro.filter.ServiceLocatorParams;
import io.zero88.qwe.micro.transfomer.RecordOutput;
import io.zero88.qwe.micro.transfomer.RecordTransformer.RecordView;
import io.zero88.qwe.micro.type.ServiceKind;
import io.zero88.qwe.micro.type.ServiceScope;

import lombok.NonNull;

/**
 * Helps invoking event service by delegating to {@code service locator} finds service by name then executes with given
 * {@code event action} and {@code request data}
 *
 * @see <a href="https://en.wikipedia.org/wiki/Remote_procedure_call">Remote procedure call</a>
 * @since 1.0.0
 */
public interface GatewayServiceInvoker extends RemoteServiceInvoker {

    /**
     * Gateway index address
     *
     * @return gateway index address
     * @since 1.0.0
     */
    @NonNull String gatewayAddress();

    /**
     * Defines service scope to discover
     *
     * @return service scope. Default: {@link ServiceScope#ALL}
     * @since 1.0.0
     */
    @NonNull
    default ServiceScope scope() {
        return ServiceScope.ALL;
    }

    /**
     * Defines service kind to discover
     *
     * @return service kind. Default: {@link ServiceKind#LOCAL}
     * @since 1.0.0
     */
    @NonNull
    default ServiceKind kind() {
        return ServiceKind.LOCAL;
    }

    /**
     * Destination name
     *
     * @return destination name
     * @since 1.0.0
     */
    @NonNull String destination();

    /**
     * Destination service pattern
     *
     * @return service pattern. Default: {@link EventPattern#REQUEST_RESPONSE}
     * @since 1.0.0
     */
    @NonNull
    default EventPattern pattern() {
        return EventPattern.REQUEST_RESPONSE;
    }

    /**
     * Defines whether throw exception if response is error
     *
     * @return {@code true} if want to throw exception
     * @apiNote Default is {@code false}
     */
    default boolean throwIfResponseError() {
        return false;
    }

    /**
     * Do search {@code destination service}.
     *
     * @param action event action to execute
     * @return destination address
     * @since 1.0.0
     */
    default Single<String> search(@NonNull EventAction action) {
        final RequestData searchReq = RequestData.builder()
                                                 .body(new JsonObject().put(ServiceLocatorParams.IDENTIFIER,
                                                                            destination()))
                                                 .filter(
                                                     new JsonObject().put(ServiceLocatorParams.BY, ByPredicate.BY_NAME)
                                                                     .put(ServiceLocatorParams.STATUS, Status.UP)
                                                                     .put(ServiceLocatorParams.SCOPE, scope())
                                                                     .put(ServiceLocatorParams.KIND, kind())
                                                                     .put(ServiceLocatorParams.VIEW,
                                                                          RecordView.TECHNICAL)
                                                                     .put(ServiceLocatorParams.ACTION, action.action()))
                                                 .build();
        final Single<EventMessage> invoker = invoke(gatewayAddress(), EventAction.GET_ONE, searchReq);
        return invoker.flatMap(out -> out.isError()
                                      ? Single.error(notFound().apply(out.getError()))
                                      : Single.just(Optional.ofNullable(out.getData()).orElse(new JsonObject())))
                      .map(json -> json.getString(RecordOutput.Fields.location))
                      .filter(Strings::isNotBlank)
                      .switchIfEmpty(Single.error(new ServiceNotFoundException(
                          "Not found destination address of service name '" + destination() + "'")));
    }

    /**
     * Do invoke remote service
     *
     * @param action given event action
     * @param data   given request body data
     * @return single result from remote service
     * @apiNote result can be single error if remote service not found or not compatible with given event action
     * @apiNote It is equivalent to call {@link #execute(EventAction, RequestData)} with given {@code data} will be
     *     {@code body} of {@code request data}
     * @see EventAction
     * @see RequestData
     * @since 1.0.0
     */
    default Single<JsonObject> execute(@NonNull EventAction action, JsonObject data) {
        return execute(action, RequestData.builder().body(data).build());
    }

    /**
     * Do invoke remote service
     *
     * @param action  given event action
     * @param reqData given request data
     * @return json result can be single error if remote service not found or not compatible with given event action
     * @apiNote Search destination address via {@link #search(EventAction)} then {@link #execute(String,
     *     EventAction, RequestData)}
     * @see EventAction
     * @see RequestData
     * @since 1.0.0
     */
    default Single<JsonObject> execute(@NonNull EventAction action, @NonNull RequestData reqData) {
        return search(action).flatMap(address -> execute(address, action, reqData));
    }

    /**
     * Do invoke remote service.
     *
     * @param address the address
     * @param action  the action
     * @param reqData the req data
     * @return json result
     * @since 1.0.0
     */
    default Single<JsonObject> execute(@NonNull String address, @NonNull EventAction action,
                                       @NonNull RequestData reqData) {
        return this.invoke(address, action, reqData).map(out -> {
            if (out.isError() && throwIfResponseError()) {
                throw new CarlException(out.getError().getCode(), out.getError().getMessage());
            }
            return Optional.ofNullable(out.getError())
                           .map(ErrorMessage::toJson)
                           .orElseGet(() -> Optional.ofNullable(out.getData()).orElseGet(JsonObject::new));
        });
    }

    /**
     * Not found converter function.
     *
     * @return converter function
     * @since 1.0.0
     */
    default Function<ErrorMessage, CarlException> notFound() {
        return msg -> ErrorMessageConverter.override(msg, ServiceNotFoundException.CODE,
                                                     RemoteServiceInvoker.notFoundMessage(serviceLabel()));
    }

}
