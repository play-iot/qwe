package io.github.zero88.qwe.http.event;

import io.github.zero88.qwe.event.EventPattern;
import io.github.zero88.qwe.micro.metadata.EventMethodDefinition;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Define Event address and its Event Method mapping
 *
 * @see EventMethodDefinition
 */
@Getter
@Builder(builderClassName = "Builder")
@EqualsAndHashCode(doNotUseGetters = true, onlyExplicitlyIncluded = true)
public final class RestEventApiMetadata {

    @NonNull
    @EqualsAndHashCode.Include
    private final String address;
    @NonNull
    private final EventPattern pattern;
    @NonNull
    private final EventMethodDefinition definition;

}
