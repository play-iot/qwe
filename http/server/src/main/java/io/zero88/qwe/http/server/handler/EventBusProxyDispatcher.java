package io.zero88.qwe.http.server.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.auth.UserInfo;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventMessage;

/**
 * @param <R> Type of response
 */
public interface EventBusProxyDispatcher<R> extends RequestDispatcher<RequestData, R> {

    EventBusProxyDispatcher<R> setup(HttpEBDispatcher dispatcher, ReqAuthDefinition authDefinition);

    HttpEBDispatcher dispatcher();

    R convertResponse(EventMessage resp);

    @Override
    default RequestInterceptor<RequestData> validator() {
        return RequestDataInterceptor.create();
    }

    @Override
    default Future<R> proceed(RoutingContext context, UserInfo userInfo, RequestData reqData) {
        return dispatcher().init(context).dispatch(reqData, userInfo).map(this::convertResponse);
    }

    interface EventMessageResponseDispatcher extends EventBusProxyDispatcher<EventMessage> {


        @Override
        default ResponseInterceptor<EventMessage> responseInterceptor() {
            return new ResponseEventInterceptor();
        }

        @Override
        default EventMessage convertResponse(EventMessage resp) {
            return resp;
        }

    }

}
