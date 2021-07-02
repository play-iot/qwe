package io.zero88.qwe.micro.servicetype;

import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.EventMethodDefinition;

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
        //        EventReplyHandler handler = EventReplyHandler.builder()
        //                                                     .system("SERVICE_DISCOVERY")
        //                                                     .address(address)
        //                                                     .action(EventAction.RETURN)
        //                                                     .success(msg -> dataConsumer.accept(ResponseData.from
        //                                                    (msg)))
        //                                                     .exception(errorConsumer)
        //                                                     .build();
        return EventBusClient.create(sharedData).request(address, EventMessage.initial(action, requestData), options);
    }

}
