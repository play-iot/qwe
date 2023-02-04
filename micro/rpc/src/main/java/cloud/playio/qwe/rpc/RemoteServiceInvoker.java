package cloud.playio.qwe.rpc;

import io.vertx.core.Future;
import cloud.playio.qwe.HasSharedData;
import cloud.playio.qwe.micro.GatewayHeaders;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventBusProxy;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.exceptions.ServiceNotFoundException;

import lombok.NonNull;

/**
 * Remote procedure call by eventbus mechanism
 *
 * @see <a href="https://en.wikipedia.org/wiki/Remote_procedure_call">Remote procedure call</a>
 * @since 1.0.0
 */
public interface RemoteServiceInvoker extends EventBusProxy, HasSharedData {

    /**
     * Request by string.
     *
     * @param serviceName the service name
     * @return the string
     * @since 1.0.0
     */
    static String requestBy(@NonNull String serviceName) {
        return "service/" + serviceName;
    }

    /**
     * Not found message string.
     *
     * @param serviceLabel the service label
     * @return the string
     * @since 1.0.0
     */
    static String notFoundMessage(String serviceLabel) {
        return serviceLabel + " is not found or out of service. Try again later";
    }

    /**
     * Request service name
     *
     * @return request service name
     * @since 1.0.0
     */
    String requester();

    /**
     * Defines remote service label that is used in case making  an intuitive error message
     *
     * @return remote service label. Default: {@code Remote service}
     * @since 1.0.0
     */
    default String serviceLabel() {
        return "Remote service";
    }

    /**
     * Invokes remote address.
     *
     * @param address the address
     * @param action  the action
     * @param reqData the req data
     * @return the single
     * @since 1.0.0
     */
    default Future<EventMessage> invoke(@NonNull String address, @NonNull EventAction action,
                                        @NonNull RequestData reqData) {
        reqData.headers().put(GatewayHeaders.X_REQUEST_BY, RemoteServiceInvoker.requestBy(requester()));
        return invoke(address, EventMessage.initial(action, reqData));
    }

    /**
     * Invokes remote address.
     *
     * @param address the address
     * @param message the message
     * @return the single
     * @since 1.0.0
     */
    default Future<EventMessage> invoke(@NonNull String address, @NonNull EventMessage message) {
        return transporter().request(address, message).otherwise(t -> {
            throw new ServiceNotFoundException(notFoundMessage(serviceLabel()), t);
        });
    }

    @Override
    default EventBusClient transporter() {
        return EventBusClient.create(sharedData());
    }

}
