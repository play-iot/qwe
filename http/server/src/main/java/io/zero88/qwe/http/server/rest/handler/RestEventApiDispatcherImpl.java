package io.zero88.qwe.http.server.rest.handler;

import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.eventbus.EventMessage;
import io.zero88.qwe.http.server.converter.RequestDataConverter;
import io.zero88.qwe.http.server.handler.EventMessageResponseHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents for pushing data via {@code EventBus} then listen {@code reply message}. After receiving {@code reply
 * message}, redirect it to {@code next Context handler}
 *
 * @see EventMessageResponseHandler
 */
@RequiredArgsConstructor
public class RestEventApiDispatcherImpl implements RestEventApiDispatcher {

    @Getter
    @Accessors(fluent = true)
    private EventBusClient transporter;
    private DeliveryEvent deliveryEvent;
    private String sharedKey;

    @Override
    public RestEventApiDispatcher setup(DeliveryEvent deliveryEvent, String sharedKey) {
        this.deliveryEvent = deliveryEvent;
        this.sharedKey = sharedKey;
        return this;
    }

    @Override
    public void handle(RoutingContext context) {
        transporter = EventBusClient.create(SharedDataLocalProxy.create(context.vertx(), sharedKey));
        EventMessage msg = deliveryEvent.isUseRequestData()
                           ? EventMessage.initial(deliveryEvent.getAction(), RequestDataConverter.convert(context))
                           : EventMessage.initial(deliveryEvent.getAction(), RequestDataConverter.body(context));
        dispatch(context, deliveryEvent.getAddress(), deliveryEvent.getPattern(),
                 msg.setUserInfo(RestEventApiDispatcher.convertUser(context.user())));
    }

}
