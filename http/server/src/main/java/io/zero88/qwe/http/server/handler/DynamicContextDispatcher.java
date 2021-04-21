package io.zero88.qwe.http.server.handler;

import java.util.function.Predicate;
import java.util.function.Supplier;

import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.exceptions.HttpException;
import io.zero88.qwe.http.HttpStatusMapping;
import io.zero88.qwe.http.HttpUtils;
import io.zero88.qwe.http.server.rest.api.DynamicEventRestApi;
import io.zero88.qwe.http.server.rest.api.DynamicHttpRestApi;
import io.zero88.qwe.http.server.rest.api.DynamicRestApi;
import io.zero88.qwe.micro.ServiceDiscoveryInvoker;
import io.github.zero88.utils.Urls;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.servicediscovery.Record;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents for {@code HTTP request} dispatcher between {@code client} and {@code micro service owner}
 * <p>
 * It's responsible for keeping {@code micro REST API} definition to handle an incoming request context then forwarding
 * to {@code micro service owner}. After receiving {@code micro service owner} response, it will return back result to
 * client
 *
 * @param <T> Dynamic REST API
 * @see DynamicRestApi
 */
public interface DynamicContextDispatcher<T extends DynamicRestApi> extends Handler<RoutingContext>, Supplier<T> {

    @SuppressWarnings("unchecked")
    static <T extends DynamicRestApi> DynamicContextDispatcher<T> create(@NonNull T api, String gatewayPath,
                                                                         ServiceDiscoveryInvoker dispatcher) {
        if (api instanceof DynamicHttpRestApi) {
            return new DynamicHttpApiDispatcher((DynamicHttpRestApi) api, gatewayPath, dispatcher);
        }
        if (api instanceof DynamicEventRestApi) {
            return new DynamicEventApiDispatcher((DynamicEventRestApi) api, gatewayPath, dispatcher);
        }
        return null;
    }

    /**
     * Handle incoming request
     *
     * @param context Request context
     */
    @Override
    default void handle(RoutingContext context) {
        HttpMethod httpMethod = validateMethod(context.request().method());
        String path = context.request().path();
        String servicePath = Urls.normalize(path.replaceAll("^" + getGatewayPath(), ""));
        this.handle(httpMethod, servicePath, context)
            .subscribe(r -> handleSuccess(context, r), t -> handleError(context, ErrorMessage.parse(t)));
    }

    /**
     * Service dispatcher
     *
     * @return service dispatcher
     * @see ServiceDiscoveryInvoker
     */
    @NonNull ServiceDiscoveryInvoker getDispatcher();

    /**
     * Gateway service path
     *
     * @return gateway service path
     */
    @NonNull String getGatewayPath();

    /**
     * Handle incoming request then dispatch to a {@code micro service owner}
     *
     * @param httpMethod HTTP method
     * @param path       url path
     * @param context    Request context
     * @return single response data
     * @see ResponseData
     */
    Single<ResponseData> handle(HttpMethod httpMethod, String path, RoutingContext context);

    /**
     * Filter {@code micro service owner} based on incoming request
     *
     * @param method Current request {@code HTTP Method}
     * @param path   Current request {@code HTTP Path}
     * @return filter function
     */
    default Predicate<Record> filter(HttpMethod method, String path) {
        return record -> record.getName().equals(get().name());
    }

    /**
     * Handle success response from {@code micro service owner}
     *
     * @param context      Routing context
     * @param responseData Response data
     */
    default void handleSuccess(RoutingContext context, ResponseData responseData) {
        context.response()
               .setStatusCode(HttpStatusMapping.success(context.request().method()).code())
               .end(HttpUtils.prettify(responseData.body(), context.request()));
    }

    /**
     * Handle error response from {@code micro service owner}
     *
     * @param context      Routing context
     * @param errorMessage Response data
     */
    default void handleError(@NonNull RoutingContext context, ErrorMessage errorMessage) {
        context.response()
               .setStatusCode(HttpStatusMapping.error(context.request().method(), errorMessage.getCode()).code())
               .end(HttpUtils.prettify(errorMessage.toJson(), context.request()));
    }

    default HttpMethod validateMethod(HttpMethod method) {
        if (get().availableMethods().contains(method)) {
            return method;
        }
        throw new HttpException("Not support HTTP Method " + method);
    }

    @RequiredArgsConstructor
    abstract class AbstractDynamicContextDispatcher<T extends DynamicRestApi> implements DynamicContextDispatcher<T> {

        private final T api;
        @Getter
        private final String gatewayPath;
        @Getter
        private final ServiceDiscoveryInvoker dispatcher;

        @Override
        public T get() { return api; }

    }

}
