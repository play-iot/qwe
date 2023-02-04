package cloud.playio.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

public final class ByEndpointPredicateFactory implements ByPredicateFactory {

    @Override
    public String by() {
        return ByPredicateFactory.BY_ENDPOINT;
    }

    @Override
    public Predicate<Record> apply(String identifier, SearchFlag searchFlag, JsonObject filter) {
        return record -> identifier.equals(record.getLocation().getString(Record.ENDPOINT));
    }

}
