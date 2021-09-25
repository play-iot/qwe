package io.zero88.qwe.http.server.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.auth.ReqAuthDefinition;
import io.zero88.qwe.auth.UserInfo;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.eventbus.EventMessage;

public interface EventBusProxyDispatcher<T> extends RequestDispatcher<RequestData, T> {

    EventBusProxyDispatcher<T> setup(HttpEBDispatcher dispatcher, ReqAuthDefinition authDefinition);

    HttpEBDispatcher getDispatcher();

    T convert(EventMessage resp);

    @Override
    default RequestInterceptor<RequestData> validator() {
        return RequestDataInterceptor.create();
    }

    @Override
    default Future<T> proceed(RoutingContext context, UserInfo userInfo, RequestData reqData) {
        return getDispatcher().init(context).dispatch(reqData, userInfo).map(this::convert);
    }

}
