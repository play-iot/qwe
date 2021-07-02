package io.zero88.qwe.http.server.handler;

import io.github.zero88.utils.Urls;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.GatewayHeadersBuilder;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.http.HttpException;
import io.zero88.qwe.http.HttpStatusMapping;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.converter.RequestDataConverter;
import io.zero88.qwe.http.server.rest.api.DynamicRestApi;
import io.zero88.qwe.micro.ServiceDiscoveryApi;
import io.zero88.qwe.micro.filter.ByPredicateFactory;
import io.zero88.qwe.micro.filter.RecordPredicateFactory;
import io.zero88.qwe.micro.filter.ServiceFilterParam;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents for {@code HTTP request} dispatcher in {@code Gateway} that forward request from a {@code client} to a
 * backend {@code micro-service}.
 * <p>
 * It's responsible for keeping {@code micro REST API} definition to handle an incoming request context then forwarding
 * to {@code micro service owner}. After receiving {@code micro service owner} response, it will return back result to
 * client
 *
 * @see DynamicRestApi
 */
public interface DynamicContextDispatcher extends Handler<RoutingContext> {

    static DynamicContextDispatcher create(@NonNull DynamicRestApi api, ServiceDiscoveryApi dispatcher,
                                           String gatewayPath) {
        return new DynamicContextDispatcherImpl(api, gatewayPath, dispatcher);
    }

    /**
     * Dynamic rest API
     *
     * @return api
     * @see DynamicRestApi
     */
    @NonNull DynamicRestApi api();

    /**
     * Service dispatcher
     *
     * @return service dispatcher
     * @see ServiceDiscoveryApi
     */
    @NonNull ServiceDiscoveryApi dispatcher();

    /**
     * Gateway service path
     *
     * @return gateway service path
     */
    @NonNull String gatewayPath();

    /**
     * Handle incoming request
     *
     * @param context Request context
     */
    @Override
    default void handle(RoutingContext context) {
        final RequestData reqData = normalizeHeader(context, createRequestData(context));
        dispatcher().execute(createFilter(reqData), reqData)
                    .map(r -> handleSuccess(context, r))
                    .otherwise(t -> handleError(context, ErrorMessage.parse(t)));
    }

    /**
     * Convert request data from request context
     *
     * @param context request context
     * @return request data
     */
    default RequestData createRequestData(RoutingContext context) {
        return api().useRequestData()
               ? RequestDataConverter.convert(context)
               : RequestData.builder().body(RequestDataConverter.body(context)).build();
    }

    /**
     * Normalize request header as gateway context based on request context
     *
     * @param context request context
     * @param reqData request data
     * @return request data
     */
    default RequestData normalizeHeader(RoutingContext context, RequestData reqData) {
        final String originPath = context.request().path();
        final String servicePath = Urls.normalize(originPath.replaceAll("^" + gatewayPath(), ""));
        final JsonObject headers = reqData.headers();
        return reqData.setHeaders(new GatewayHeadersBuilder(headers).addCorrelationId()
                                                                    .addForwardedProto(context.request().scheme())
                                                                    .addForwardedHost(context.request().host())
                                                                    .addForwardedMethod(getMethod(context))
                                                                    .addForwardedURI(originPath)
                                                                    .addRequestURI(servicePath)
                                                                    .getHeaders());
    }

    /**
     * Create a gateway filter based on incoming request to find a {@code micro service owner}
     *
     * @param reqData current request data
     * @return request filter
     * @see RecordPredicateFactory
     */
    default RequestFilter createFilter(@NonNull RequestData reqData) {
        final RequestFilter filter = new RequestFilter(reqData.filter());
        return filter.put(ServiceFilterParam.TYPE, api().serviceType())
                     .put(ServiceFilterParam.NAME, api().name())
                     .put(ServiceFilterParam.BY, ByPredicateFactory.BY_PATH)
                     .put(ServiceFilterParam.IDENTIFIER,
                          reqData.headers().getString(GatewayHeadersBuilder.X_REQUEST_URI));
    }

    /**
     * Handle success response from {@code micro service owner}
     *
     * @param context      Routing context
     * @param responseData Response data
     * @return future
     */
    default Future<Void> handleSuccess(RoutingContext context, ResponseData responseData) {
        return context.response()
                      .setStatusCode(HttpStatusMapping.success(context.request().method()).code())
                      .end(HttpUtils.prettify(responseData.body(), context.request()));
    }

    /**
     * Handle error response from {@code micro service owner}
     *
     * @param context      Routing context
     * @param errorMessage Response error data
     * @return future
     */
    default Future<Void> handleError(@NonNull RoutingContext context, ErrorMessage errorMessage) {
        return context.response()
                      .setStatusCode(HttpStatusMapping.error(context.request().method(), errorMessage.getCode()).code())
                      .end(HttpUtils.prettify(errorMessage.toJson(), context.request()));
    }

    default HttpMethod getMethod(RoutingContext context) {
        HttpMethod method = context.request().method();
        if (api().availableMethods().contains(method)) {
            return method;
        }
        throw new HttpException("Not support HTTP Method " + method);
    }

    @Accessors(fluent = true)
    @RequiredArgsConstructor
    class DynamicContextDispatcherImpl implements DynamicContextDispatcher {

        @Getter
        private final DynamicRestApi api;
        @Getter
        private final String gatewayPath;
        @Getter
        private final ServiceDiscoveryApi dispatcher;

    }

}
