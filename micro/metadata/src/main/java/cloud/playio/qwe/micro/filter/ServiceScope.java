package cloud.playio.qwe.micro.filter;

import java.util.function.Predicate;
import java.util.stream.Stream;

import cloud.playio.qwe.micro.servicetype.EventMessageHttpService;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceScope {

    PUBLIC(ServiceScope::isPublic), INTERNAL(record -> !isPublic(record)), ALL(record -> true);

    @Getter
    private final Predicate<Record> predicate;

    public static ServiceScope parse(String scope) {
        return Stream.of(ServiceScope.values())
                     .filter(serviceScope -> serviceScope.name().equalsIgnoreCase(scope))
                     .findFirst()
                     .orElse(PUBLIC);
    }

    private static boolean isPublic(Record record) {
        return HttpEndpoint.TYPE.equalsIgnoreCase(record.getType()) ||
               EventMessageHttpService.TYPE.equalsIgnoreCase(record.getType());
    }
}
