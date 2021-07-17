package io.zero88.qwe.micro.executor;

import io.github.zero88.utils.HttpScheme;
import io.github.zero88.utils.Urls;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;
import io.zero88.qwe.SharedDataLocalProxy;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.dto.msg.RequestFilter;
import io.zero88.qwe.dto.msg.ResponseData;
import io.zero88.qwe.http.client.HttpClientDelegate;
import io.zero88.qwe.micro.GatewayHeaders;

public final class HttpClientServiceExecutor implements ServiceExecutor {

    @Override
    public String serviceType() {
        return HttpEndpoint.TYPE;
    }

    @Override
    public JsonObject getConfiguration(Record record, SharedDataLocalProxy sharedData) {
        return null;
    }

    @Override
    public Future<ResponseData> execute(ServiceReference serviceReference, RequestData requestData,
                                        RequestFilter filter) {
        GatewayHeaders headers = new GatewayHeaders(requestData.headers());
        HttpLocation loc = new HttpLocation(serviceReference.record().getLocation());
        String endpoint = Urls.combinePath(
            Urls.buildURL(loc.isSsl() ? HttpScheme.HTTPS : HttpScheme.HTTP, loc.getHost(), loc.getPort()),
            headers.getRequestURI());
        return HttpClientDelegate.create(serviceReference.getAs(HttpClient.class))
                                 .request(endpoint, headers.getForwardedMethod(), requestData, false);
    }

}
