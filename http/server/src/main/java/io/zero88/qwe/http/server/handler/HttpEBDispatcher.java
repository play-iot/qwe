package io.zero88.qwe.http.server.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.HasLogger;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.auth.UserInfo;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.DeliveryEvent;
import io.zero88.qwe.eventbus.EventBusClient;
import io.zero88.qwe.eventbus.EventBusProxy;
import io.zero88.qwe.eventbus.EventMessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpEBDispatcher implements EventBusProxy, HasLogger {

    private final String sharedKey;
    private final DeliveryEvent deliveryEvent;
    @Getter
    @Accessors(fluent = true)
    private EventBusClient transporter;

    public static HttpEBDispatcher create(String sharedKey, DeliveryEvent event) {
        return new HttpEBDispatcher(sharedKey, event);
    }

    public HttpEBDispatcher init(RoutingContext context) {
        transporter = EventBusClient.create(SharedDataLocalProxy.create(context.vertx(), sharedKey));
        return this;
    }

    public Future<EventMessage> dispatch(RequestData reqData, UserInfo userInfo) {
        logger().info("Dispatch HTTP request to address[{}::{}]", deliveryEvent.action(), deliveryEvent.address());
        EventMessage msg = deliveryEvent.useRequestData()
                           ? EventMessage.initial(deliveryEvent.action(), reqData)
                           : EventMessage.initial(deliveryEvent.action(), reqData.body());
        return transporter().fire(deliveryEvent.address(), deliveryEvent.pattern(), msg.setUserInfo(userInfo));
    }

}
