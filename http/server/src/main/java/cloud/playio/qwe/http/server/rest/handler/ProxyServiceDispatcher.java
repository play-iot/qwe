package cloud.playio.qwe.http.server.rest.handler;

import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpMethod;

import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.http.server.HttpSystem.GatewaySystem;
import cloud.playio.qwe.http.server.handler.RequestDispatcher;
import cloud.playio.qwe.http.server.handler.ResponseDataInterceptor;
import cloud.playio.qwe.http.server.handler.ResponseInterceptor;
import cloud.playio.qwe.http.server.rest.api.ProxyServiceApi;
import cloud.playio.qwe.micro.ServiceDiscoveryApi;

/**
 * Represents for {@code HTTP request} dispatcher in {@code Gateway} that forward request from a {@code client} to a
 * backend {@code micro-service}.
 * <p>
 * It's responsible for keeping {@code micro REST API} definition to handle an incoming request context then forwarding
 * to {@code micro service owner}. After receiving {@code micro service owner} response, it will return result to
 * client.
 *
 * @see ProxyServiceApi
 */
public interface ProxyServiceDispatcher extends RequestDispatcher<RequestData, ResponseData>, HasLogger, GatewaySystem {

    static ProxyServiceDispatcher create(ServiceDiscoveryApi dispatcher, String gatewayPath,
                                         ReqAuthDefinition authDefinition,
                                         BiFunction<String, HttpMethod, RequestFilter> filterFun) {
        return new ProxyServiceDispatcherImpl().setup(dispatcher, gatewayPath, authDefinition, filterFun);
    }

    @Override
    default Logger logger() {
        return LogManager.getLogger(ProxyServiceDispatcher.class);
    }

    /**
     * Setup dispatcher
     *
     * @param dispatcher     the proxy service dispatcher
     * @param gatewayPath    the gateway path
     * @param authDefinition the auth definition
     * @param filterFun      the proxy path
     * @return a reference to this for fluent API
     */
    ProxyServiceDispatcher setup(ServiceDiscoveryApi dispatcher, String gatewayPath, ReqAuthDefinition authDefinition,
                                 BiFunction<String, HttpMethod, RequestFilter> filterFun);

    @Override
    default ResponseInterceptor<ResponseData> responseInterceptor() {
        return new ResponseDataInterceptor();
    }

}
