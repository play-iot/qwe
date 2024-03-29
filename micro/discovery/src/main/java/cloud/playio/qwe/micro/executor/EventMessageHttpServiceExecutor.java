package cloud.playio.qwe.micro.executor;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.ErrorMessageConverter;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventMessage;
import cloud.playio.qwe.http.EventMethodDefinition;
import cloud.playio.qwe.micro.GatewayHeaders;
import cloud.playio.qwe.micro.filter.ServiceFilterParam;
import cloud.playio.qwe.micro.servicetype.EventMessageHttpService;
import cloud.playio.qwe.micro.servicetype.EventMessagePusher;

import lombok.NonNull;

public final class EventMessageHttpServiceExecutor implements ServiceExecutor {

    @Override
    public String serviceType() {
        return EventMessageHttpService.TYPE;
    }

    @Override
    public JsonObject getConfiguration(Record record, SharedDataLocalProxy sharedData) {
        return new JsonObject().put(EventMessageHttpService.SHARED_KEY_CONFIG, sharedData.sharedKey())
                               .put(EventMessageHttpService.DELIVERY_OPTIONS_CONFIG, null);
    }

    @Override
    public Future<ResponseData> execute(SharedDataLocalProxy sharedData, ServiceReference serviceReference,
                                        RequestData reqData, RequestFilter filter) {
        return serviceReference.getAs(EventMessagePusher.class)
                               .execute(Optional.ofNullable(filter.getString(ServiceFilterParam.ACTION))
                                                .map(EventAction::parse)
                                                .orElseGet(() -> findActionInHeader(serviceReference, reqData)),
                                        reqData)
                               .map(this::from);
    }

    private EventAction findActionInHeader(ServiceReference serviceReference, RequestData reqData) {
        GatewayHeaders headers = new GatewayHeaders(reqData.headers());
        return EventMethodDefinition.from(serviceReference.record().getLocation())
                                    .search(headers.getRequestURI(), headers.getForwardedMethod());
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
