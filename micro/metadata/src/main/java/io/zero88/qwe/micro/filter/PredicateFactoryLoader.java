package io.zero88.qwe.micro.filter;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vertx.core.ServiceHelper;

import lombok.Getter;

public final class PredicateFactoryLoader {

    private static PredicateFactoryLoader instance;

    public static PredicateFactoryLoader instance() {
        if (Objects.nonNull(instance)) {
            return instance;
        }
        synchronized (PredicateFactoryLoader.class) {
            if (Objects.nonNull(instance)) {
                return instance;
            }
            return instance = new PredicateFactoryLoader();
        }
    }

    /**
     * Includes record predicate factory
     *
     * @see ByPredicateFactory
     * @see SimplePredicateFactory
     * @see CustomPredicateFactory
     */
    @Getter
    private final Collection<RecordPredicateFactory> predicatesFactories;

    private PredicateFactoryLoader() {
        this.predicatesFactories = Stream.of(ServiceHelper.loadFactories(DefaultPredicateFactory.class),
                                             ServiceHelper.loadFactories(RecordPredicateFactory.class))
                                         .flatMap(Collection::stream)
                                         .collect(Collectors.toSet());
    }

}
