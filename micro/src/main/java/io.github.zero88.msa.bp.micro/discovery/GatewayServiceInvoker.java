package io.github.zero88.msa.bp.micro.discovery;

import java.util.Optional;
import java.util.function.Function;

import io.github.zero88.msa.bp.dto.ErrorMessage;
import io.github.zero88.msa.bp.dto.msg.RequestData;
import io.github.zero88.msa.bp.event.EventAction;
import io.github.zero88.msa.bp.event.EventMessage;
import io.github.zero88.msa.bp.event.EventPattern;
import io.github.zero88.msa.bp.exceptions.BlueprintException;
import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.ServiceNotFoundException;
import io.github.zero88.msa.bp.exceptions.converter.ErrorMessageConverter;
import io.github.zero88.msa.bp.micro.ServiceGatewayIndex.Params;
import io.github.zero88.msa.bp.micro.ServiceKind;
import io.github.zero88.msa.bp.micro.ServiceScope;
import io.github.zero88.msa.bp.micro.filter.ByPredicate;
import io.github.zero88.msa.bp.micro.transfomer.RecordOutput;
import io.github.zero88.msa.bp.micro.transfomer.RecordTransformer.RecordView;
import io.github.zero88.utils.Strings;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Status;

import lombok.NonNull;

/**
 * Helps invoking event service by delegating to {@code service gateway} finds service by name then executes with given
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
     * Do search {@code destination service}.
     *
     * @param action event action to execute
     * @return destination address
     * @since 1.0.0
     */
    default Single<String> search(@NonNull EventAction action) {
        final RequestData searchReq = RequestData.builder()
                                                 .body(new JsonObject().put(Params.IDENTIFIER, destination()))
                                                 .filter(new JsonObject().put(Params.BY, ByPredicate.BY_NAME)
                                                                         .put(Params.STATUS, Status.UP)
                                                                         .put(Params.SCOPE, scope())
                                                                         .put(Params.KIND, kind())
                                                                         .put(Params.VIEW, RecordView.TECHNICAL)
                                                                         .put(Params.ACTION, action))
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
        return invoke(address, action, reqData).map(out -> out.isError() ? out.getError().toJson() : out.getData());
    }

    /**
     * Not found converter function.
     *
     * @return converter function
     * @since 1.0.0
     */
    default Function<ErrorMessage, BlueprintException> notFound() {
        return msg -> ErrorMessageConverter.from(msg, ErrorCode.SERVICE_NOT_FOUND,
                                                 RemoteServiceInvoker.notFoundMessage(serviceLabel()));
    }

}
