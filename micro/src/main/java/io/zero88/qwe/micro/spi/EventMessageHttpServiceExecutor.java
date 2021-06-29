package io.zero88.qwe.micro.spi;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.ErrorMessageConverter;
import io.zero88.qwe.dto.msg.GatewayHeadersBuilder;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.event.EventAction;
import io.zero88.qwe.event.EventMessage;
import io.zero88.qwe.http.EventMethodDefinition;
import io.zero88.qwe.micro.ServiceExecutor;
import io.zero88.qwe.micro.servicetype.EventMessageHttpService;
import io.zero88.qwe.micro.servicetype.EventMessagePusher;

import lombok.NonNull;

public final class EventMessageHttpServiceExecutor implements ServiceExecutor {

    @Override
    public String serviceType() {
        return EventMessageHttpService.TYPE;
    }

    @Override
    public JsonObject getConfiguration(Record record, SharedDataLocalProxy sharedDataLocalProxy) {
        return new JsonObject().put(EventMessageHttpService.SHARED_KEY_CONFIG, sharedDataLocalProxy.getSharedKey())
                               .put(EventMessageHttpService.DELIVERY_OPTIONS_CONFIG, null);
    }

    @Override
    public Future<ResponseData> execute(ServiceReference serviceReference, RequestData reqData) {
        GatewayHeadersBuilder headers = new GatewayHeadersBuilder(reqData.headers());
        EventMethodDefinition definition = EventMethodDefinition.from(serviceReference.record().getLocation());
        EventAction action = definition.search(headers.getRequestURI(), headers.getForwardedMethod());
        return serviceReference.getAs(EventMessagePusher.class).execute(action, reqData).map(this::from);
    }

    private ResponseData from(@NonNull EventMessage message) {
        ResponseData responseData = new ResponseData();
        responseData.setHeaders(new JsonObject().put("status", message.getStatus())
                                                .put("action", message.getAction().action())
                                                .put("prevAction", Optional.ofNullable(message.getPrevAction())
                                                                           .map(EventAction::action)
                                                                           .orElse(null)));
        if (message.isError()) {
            throw ErrorMessageConverter.convert(message.getError());
        }
        return responseData.setBody(message.getData());
    }

}
