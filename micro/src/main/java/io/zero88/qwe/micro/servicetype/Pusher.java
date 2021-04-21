package io.zero88.qwe.micro.servicetype;

import java.util.function.Consumer;

import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.event.EventbusClient;
import io.zero88.qwe.event.ReplyEventHandler;
import io.zero88.qwe.micro.http.EventMethodDefinition;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Pusher implements EventMessagePusher {

    @NonNull
    private final EventbusClient eventbus;
    @NonNull
    private final EventMethodDefinition definition;
    @NonNull
    private final DeliveryOptions options;
    @NonNull
    private final String address;

    @Override
    public void execute(String path, HttpMethod httpMethod, JsonObject requestData, Consumer<ResponseData> dataConsumer,
                        Consumer<Throwable> errorConsumer) {
        EventAction action = definition.search(path, httpMethod);
        ReplyEventHandler handler = ReplyEventHandler.builder()
                                                     .system("SERVICE_DISCOVERY")
                                                     .address(address)
                                                     .action(EventAction.RETURN)
                                                     .success(msg -> dataConsumer.accept(ResponseData.from(msg)))
                                                     .exception(errorConsumer)
                                                     .build();
        eventbus.request(address, EventMessage.initial(action, requestData), handler, options);
    }

}
