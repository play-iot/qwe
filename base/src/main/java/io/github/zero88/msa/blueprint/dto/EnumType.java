package io.github.zero88.msa.blueprint.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.github.zero88.utils.Reflections.ReflectionField;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface EnumType extends JsonData {

    static <E extends EnumType> E factory(String type, @NonNull Class<E> clazz) {
        return factory(type, clazz, null);
    }

    static <E extends EnumType> E factory(String type, @NonNull Class<E> clazz, E defaultType) {
        if (Strings.isBlank(type)) {
            return defaultType;
        }
        String t = Strings.optimizeMultipleSpace(type).toUpperCase(Locale.ENGLISH);
        return ReflectionField.streamConstants(clazz, clazz)
                              .filter(enumType -> enumType.type().equals(t) ||
                                                  Objects.nonNull(enumType.alternatives()) &&
                                                  enumType.alternatives().contains(t))
                              .findAny()
                              .orElseGet(
                                  () -> ReflectionClass.createObject(clazz, Collections.singletonMap(String.class, t)));
    }

    @JsonProperty(value = "type")
    @NonNull String type();

    @JsonProperty(value = "alternatives")
    default Collection<String> alternatives() {
        return null;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    abstract class AbstractEnumType implements EnumType {

        @NonNull
        private final String type;
        private final Collection<String> aliases;

        protected AbstractEnumType(String type) {
            this(type, (Collection<String>) null);
        }

        protected AbstractEnumType(String type, String... aliases) {
            this(type, Arrays.stream(aliases).filter(Strings::isNotBlank).collect(Collectors.toSet()));
        }

        @Override
        public final @NonNull String type() {
            return type;
        }

        @Override
        public final @NonNull Collection<String> alternatives() {
            return aliases;
        }

        @Override
        public String toString() {
            return type();
        }

    }

}
