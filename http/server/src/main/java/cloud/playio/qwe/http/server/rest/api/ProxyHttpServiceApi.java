package cloud.playio.qwe.http.server.rest.api;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.http.HttpMethod;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.HttpLocation;
import cloud.playio.qwe.auth.ReqAuthDefinition;
import cloud.playio.qwe.dto.msg.RequestFilter;
import cloud.playio.qwe.http.HttpUtils;
import cloud.playio.qwe.http.server.RoutePath;
import cloud.playio.qwe.http.server.RouterCreator;
import cloud.playio.qwe.micro.filter.ByPredicateFactory;
import cloud.playio.qwe.micro.filter.ServiceFilterParam;
import cloud.playio.qwe.utils.PriorityUtils;

public class ProxyHttpServiceApi implements ProxyServiceApi {

    private Record record;
    private HttpLocation location;

    @Override
    public String serviceType() {return HttpEndpoint.TYPE;}

    @Override
    public ProxyHttpServiceApi setup(Record record) {
        this.record = record;
        this.location = record.getLocation().mapTo(HttpLocation.class);
        return this;
    }

    @Override
    public @NotNull Collection<RoutePath> paths() {
        return Collections.singleton(
            new RoutePath(RouterCreator.addWildcards(location.getRoot()), HttpMethod.GET, ReqAuthDefinition.noAuth(),
                          HttpUtils.JSON_CONTENT_TYPES));
    }

    @Override
    public int order() {
        return PriorityUtils.priorityOrder(location.getRoot().length());
    }

    @Override
    public String name() {
        return record.getName();
    }

    @Override
    public RequestFilter createFilter(String actualPath, HttpMethod method) {
        return new RequestFilter().put(ServiceFilterParam.TYPE, serviceType())
                                  .put(ServiceFilterParam.IDENTIFIER, location.getEndpoint())
                                  .put(ServiceFilterParam.BY, ByPredicateFactory.BY_ENDPOINT);
    }

}
