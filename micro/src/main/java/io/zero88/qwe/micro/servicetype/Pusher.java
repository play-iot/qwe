package io.zero88.qwe.micro.servicetype;

import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventBusClient;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.micro.http.EventMethodDefinition;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Pusher implements EventMessagePusher {

    @NonNull
    private final EventBusClient eventbus;
    @NonNull
    private final EventMethodDefinition definition;
    @NonNull
    private final DeliveryOptions options;
    @NonNull
    private final String address;

    @Override
    public Future<ResponseData> execute(String path, HttpMethod httpMethod, JsonObject requestData) {
        EventAction action = definition.search(path, httpMethod);
        //        EventReplyHandler handler = EventReplyHandler.builder()
        //                                                     .system("SERVICE_DISCOVERY")
        //                                                     .address(address)
        //                                                     .action(EventAction.RETURN)
        //                                                     .success(msg -> dataConsumer.accept(ResponseData.from
        //                                                    (msg)))
        //                                                     .exception(errorConsumer)
        //                                                     .build();
        return eventbus.request(address, EventMessage.initial(action, requestData), options).map(ResponseData::from);
    }

}
