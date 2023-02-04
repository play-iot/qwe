package cloud.playio.qwe.http.server.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.auth.UserInfo;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventMessage;

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
