package cloud.playio.qwe.micro.filter;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import io.vertx.servicediscovery.Record;
import cloud.playio.qwe.micro.filter.FilterAttributeFinder.FilterStringFinder;

public final class ServiceTypePredicateFactory
    implements SimplePredicateFactory<String>, FilterStringFinder {

    @Override
    public @NotNull String attribute() {
        return ServiceFilterParam.TYPE;
    }

    @Override
    public Predicate<Record> apply(String type) {
        return record -> testType(record, type);
    }

    static boolean testType(Record record, String type) {
        return record.getType().equals(type);
    }

}
