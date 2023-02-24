package cloud.playio.qwe.micro.register;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;

import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.eventbus.EventBusClient;
import cloud.playio.qwe.http.EventHttpService;
import cloud.playio.qwe.micro.RecordHelper;
import cloud.playio.qwe.micro.ServiceDiscoveryApi;
import lombok.Builder;
import lombok.NonNull;

/**
 * Represents for Event http service register.
 *
 * @param <S> Type of {@code EventHttpService}
 * @see EventHttpService
 * @since 1.0.0
 */
@Builder(builderClassName = "Builder")
public final class EventHttpServiceRegister<S extends EventHttpService> {

    private static final Logger LOGGER = LogManager.getLogger(EventHttpServiceRegister.class);

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final String sharedKey;
    @NonNull
    private final Supplier<Set<S>> eventServices;
    private final Consumer<S> afterRegisterEventBusAddress;

    /**
     * Publish services to external API and register event listener by address at the same time.
     *
     * @param discovery the discovery
     * @return list {@code records}
     * @see Record
     * @since 1.0.0
     */
    public Future<List<Record>> publish(@NonNull ServiceDiscoveryApi discovery) {
        final EventBusClient client = EventBusClient.create(SharedDataLocalProxy.create(vertx, sharedKey));
        return Future.succeededFuture(eventServices.get()
                                                   .stream()
                                                   .map(s -> register(client, discovery, s))
                                                   .flatMap(cf -> cf.list()
                                                                    .stream()
                                                                    .filter(Objects::nonNull)
                                                                    .map(Record.class::cast))
                                                   .collect(Collectors.toList()))
                     .onSuccess(recs -> LOGGER.info("Published {} Service API(s)", recs.size()));
    }

    private CompositeFuture register(@NonNull EventBusClient client, @NonNull ServiceDiscoveryApi discovery,
                                     @NonNull S srv) {
        client.register(srv.address(), srv);
        Optional.ofNullable(afterRegisterEventBusAddress).ifPresent(func -> func.accept(srv));
        return CompositeFuture.join(srv.definitions()
                                       .stream()
                                       .map(d -> discovery.register(RecordHelper.create(srv.api(), srv.address(), d)))
                                       .collect(Collectors.toList()));
    }

}
