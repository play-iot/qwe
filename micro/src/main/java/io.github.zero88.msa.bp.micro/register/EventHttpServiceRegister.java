package io.github.zero88.msa.bp.micro.register;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.zero88.msa.bp.component.SharedDataDelegate;
import io.github.zero88.msa.bp.event.EventbusClient;
import io.github.zero88.msa.bp.micro.metadata.EventHttpService;
import io.github.zero88.msa.bp.micro.ServiceDiscoveryController;
import io.github.zero88.msa.bp.utils.ExecutorHelpers;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHttpServiceRegister.class);

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final String sharedKey;
    @NonNull
    private final Supplier<Set<S>> eventServices;
    private final Consumer<S> afterRegisterEventbusAddress;

    /**
     * Publish services to external API and register event listener by address at the same time.
     *
     * @param discovery the discovery
     * @return list {@code records}
     * @see Record
     * @since 1.0.0
     */
    public Single<List<Record>> publish(@NonNull ServiceDiscoveryController discovery) {
        final EventbusClient client = SharedDataDelegate.getEventController(vertx, sharedKey);
        return ExecutorHelpers.blocking(vertx, eventServices::get)
                              .flattenAsObservable(s -> s)
                              .doOnEach(s -> Optional.ofNullable(s.getValue())
                                                     .ifPresent(service -> registerEventbusAddress(client, service)))
                              .filter(s -> Objects.nonNull(s.definitions()))
                              .flatMap(s -> registerEndpoint(discovery, s))
                              .toList()
                              .doOnSuccess(r -> LOGGER.info("Published {} Service API(s)", r.size()));
    }

    private void registerEventbusAddress(@NonNull EventbusClient client, @NonNull S service) {
        client.register(service.address(), service);
        Optional.ofNullable(afterRegisterEventbusAddress).ifPresent(func -> func.accept(service));
    }

    private Observable<Record> registerEndpoint(@NonNull ServiceDiscoveryController discovery, @NonNull S service) {
        if (!discovery.isEnabled()) {
            return Observable.empty();
        }
        return Observable.fromIterable(service.definitions())
                         .flatMapSingle(e -> discovery.addEventMessageRecord(service.api(), service.address(), e));
    }

    public static class Builder<S extends EventHttpService> {

        public Builder<S> vertx(@NonNull Vertx vertx) {
            this.vertx = vertx;
            return this;
        }

        public Builder<S> vertx(@NonNull io.vertx.reactivex.core.Vertx vertx) {
            this.vertx = vertx.getDelegate();
            return this;
        }

    }

}
