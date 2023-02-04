package cloud.playio.qwe.micro.servicetype;

import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.eventbus.EventMessage;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Pusher implements EventMessagePusher {

    @NonNull
    private final SharedDataLocalProxy sharedData;
    @NonNull
    private final DeliveryOptions options;
    @NonNull
    private final String address;

    @Override
    public Future<EventMessage> execute(EventAction action, RequestData requestData) {
        return EventBusClient.create(sharedData).request(address, EventMessage.initial(action, requestData), options);
    }

}
