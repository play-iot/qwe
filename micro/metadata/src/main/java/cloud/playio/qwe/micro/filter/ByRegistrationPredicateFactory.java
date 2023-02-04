package cloud.playio.qwe.micro.filter;

import java.util.function.Predicate;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

public final class ByRegistrationPredicateFactory implements ByPredicateFactory {

    @Override
    public String by() {
        return BY_REGISTRATION;
    }

    @Override
    public Predicate<Record> apply(String registration, SearchFlag searchFlag, JsonObject filter) {
        return record -> registration.equalsIgnoreCase(record.getRegistration());
    }

}
