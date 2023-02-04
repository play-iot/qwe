package cloud.playio.qwe.micro.filter;

import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import cloud.playio.qwe.utils.JsonUtils;

/**
 * @see ServiceScope
 */
public final class ServiceScopePredicateFactory implements SimplePredicateFactory<ServiceScope> {

    @Override
    public @NotNull String attribute() {
        return ServiceFilterParam.SCOPE;
    }

    @Override
    public ServiceScope findAttribute(JsonObject filter) {
        return JsonUtils.findString(filter, attribute()).map(ServiceScope::parse).orElse(null);
    }

    @Override
    public Predicate<Record> apply(ServiceScope scope) {
        return Objects.isNull(scope) ? r -> true : scope.getPredicate();
    }

}
