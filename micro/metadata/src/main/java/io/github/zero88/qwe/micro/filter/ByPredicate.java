package io.github.zero88.qwe.micro.filter;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.github.zero88.qwe.event.EventAction;
import io.vertx.servicediscovery.Record;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ByPredicate {

    Predicate<Record> by(@NonNull String identifier);

    String BY_PATH = "PATH";
    String BY_NAME = "NAME";
    String BY_GROUP = "GROUP";
    String BY_REGISTRATION = "REGISTRATION";


    @RequiredArgsConstructor
    enum ByPredicateEnum implements ByPredicate {
        REGISTRATION(BY_REGISTRATION, null, id -> r -> id.equalsIgnoreCase(r.getRegistration())),
        GROUP_MANY(BY_GROUP, EventAction.GET_LIST, id -> r -> r.getName().toLowerCase().startsWith(id.toLowerCase())),
        GROUP_ONE(BY_GROUP, EventAction.GET_ONE, id -> r -> {
            final int idx = r.getName().lastIndexOf(".");
            return idx != -1 && r.getName().substring(0, idx).equalsIgnoreCase(id.toLowerCase());
        }),
        NAME_MANY(BY_NAME, EventAction.GET_LIST, id -> r -> r.getName().toLowerCase().contains(id.toLowerCase())),
        NAME_ONE(BY_NAME, EventAction.GET_ONE, id -> r -> r.getName().equalsIgnoreCase(id.toLowerCase())),
        PATH(BY_PATH, null, id -> record -> ByPathPredicate.predicate(record, id));

        private final String type;
        private final EventAction action;
        private final Function<String, Predicate<Record>> func;

        static ByPredicateEnum parse(@NonNull EventAction action, String by) {
            return Stream.of(ByPredicateEnum.values())
                         .filter(anEnum -> anEnum.type.equalsIgnoreCase(by) &&
                                           (action.equals(anEnum.action) || anEnum.action == null))
                         .findFirst()
                         .orElse(ByPredicateEnum.REGISTRATION);
        }

        @Override
        public Predicate<Record> by(String identifier) {
            return func.apply(identifier);
        }
    }

}
