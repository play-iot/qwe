package cloud.playio.qwe.http.server.rest.handler;

import java.util.function.BiFunction;

import io.github.zero88.utils.Urls;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.auth.UserInfo;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.http.server.handler.AuthInterceptor;
import cloud.playio.qwe.http.server.handler.RequestDataInterceptor;
import cloud.playio.qwe.http.server.handler.RequestInterceptor;
import cloud.playio.qwe.micro.GatewayHeaders;
import cloud.playio.qwe.micro.ServiceDiscoveryApi;

import lombok.Getter;
import lombok.experimental.Accessors;

public class ProxyServiceDispatcherImpl implements ProxyServiceDispatcher {

    @Getter
    @Accessors(fluent = true)
    private AuthInterceptor authInterceptor;
    private String gatewayPath;
    private ServiceDiscoveryApi dispatcher;
    private BiFunction<String, HttpMethod, RequestFilter> filterFun;

    @Override
    public ProxyServiceDispatcher setup(ServiceDiscoveryApi dispatcher, String gatewayPath,
                                        ReqAuthDefinition authDefinition,
                                        BiFunction<String, HttpMethod, RequestFilter> filterFun) {
        this.dispatcher = dispatcher;
        this.gatewayPath = gatewayPath;
        this.authInterceptor = AuthInterceptor.create(authDefinition);
        this.filterFun = filterFun;
        return this;
    }

    @Override
    public RequestInterceptor<RequestData> validator() {
        return RequestDataInterceptor.create().andThen(this::normalizeHeader);
    }

    @Override
    public Future<ResponseData> proceed(RoutingContext context, UserInfo userInfo, RequestData reqData) {
        final GatewayHeaders headers = new GatewayHeaders(reqData.headers());
        logger().info(decor("Dispatch service request [{}][{}::{}]"), headers.getCorrelationId(),
                      headers.getForwardedMethod(), headers.getRequestURI());
        return dispatcher.execute(filterFun.apply(headers.getRequestURI(), headers.getForwardedMethod()), reqData);
    }

    /**
     * Normalize request header as gateway context based on request context
     *
     * @param context request context
     * @param reqData request data
     * @return request data
     */
    protected RequestData normalizeHeader(RoutingContext context, RequestData reqData) {
        final String originPath = context.request().path();
        final String servicePath = Urls.normalize(originPath.replaceAll("^" + gatewayPath, ""));
        return reqData.setHeaders(new GatewayHeaders(reqData.headers()).addCorrelationId()
                                                                       .addForwardedProto(context.request().scheme())
                                                                       .addForwardedHost(context.request().host())
                                                                       .addForwardedMethod(context.request().method())
                                                                       .addForwardedURI(originPath)
                                                                       .addRequestURI(servicePath)
                                                                       .getHeaders());
    }

}
