package cloud.playio.qwe.micro.executor;

import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.dto.msg.ResponseData;
import cloud.playio.qwe.http.client.HttpClientWrapper;
import cloud.playio.qwe.micro.GatewayHeaders;

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
    public Future<ResponseData> execute(SharedDataLocalProxy sharedData, ServiceReference serviceReference,
                                        RequestData requestData, RequestFilter filter) {
        GatewayHeaders headers = new GatewayHeaders(requestData.headers());
        HttpLocation loc = new HttpLocation(serviceReference.record().getLocation());
        RequestOptions options = new RequestOptions().setHost(loc.getHost())
                                                     .setPort(loc.getPort())
                                                     .setSsl(loc.isSsl())
                                                     .setMethod(headers.getForwardedMethod())
                                                     .setURI(headers.getRequestURI());
        return HttpClientWrapper.wrap(serviceReference.getAs(HttpClient.class), headers.getRequestBy())
                                .request(options, requestData);
    }

}
