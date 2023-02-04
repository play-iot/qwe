package cloud.playio.qwe.http.server.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.auth.UserInfo;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.DeliveryEvent;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventBusProxy;
import cloud.playio.qwe.eventbus.EventMessage;

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
