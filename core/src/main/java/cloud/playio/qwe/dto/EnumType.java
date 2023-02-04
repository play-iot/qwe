package cloud.playio.qwe.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.zero88.repl.Arguments;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.repl.ReflectionField;
import io.github.zero88.utils.Strings;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Declares enum type that similar to {@link Enum} but able to serialize as json and extendable
 */
public interface EnumType extends JsonData, Serializable {

    /**
     * Create enum type
     * <p>
     * It is equivalent with invoke {@link #factory(String, Class, boolean)} with {@code uppercase = false}
     *
     * @param type  type in string
     * @param clazz class of EnumType
     * @param <E>   Type of EnumType
     * @return enum type
     * @see #factory(String, Class, boolean, EnumType)
     */
    static <E extends EnumType> E factory(String type, @NonNull Class<E> clazz) {
        return factory(type, clazz, false);
    }

    /**
     * Create enum type
     * <p>
     * It is equivalent with invoke {@link #factory(String, Class, boolean, EnumType)} with {@code uppercase = false}
     *
     * @param type        type in string
     * @param clazz       class of EnumType
     * @param defaultType Fallback type if {@code given type} is blank or null
     * @param <E>         Type of EnumType
     * @return enum type
     * @see #factory(String, Class, boolean, EnumType)
     */
    static <E extends EnumType> E factory(String type, @NonNull Class<E> clazz, E defaultType) {
        return factory(type, clazz, false, defaultType);
    }

    /**
     * Create enum type
     *
     * @param type      type
     * @param clazz     class of EnumType
     * @param uppercase {@code true} if force {@code given type} is uppercase, otherwise keep it as it is
     * @param <E>       Type of EnumType
     * @return enum type
     * @see #factory(String, Class, boolean, EnumType)
     */
    static <E extends EnumType> E factory(String type, @NonNull Class<E> clazz, boolean uppercase) {
        return factory(type, clazz, uppercase, null);
    }

    /**
     * Create enum type by searching {@code given type} in {@code given enum class}
     *
     * @param type        type
     * @param clazz       class of EnumType
     * @param uppercase   {@code true} if force {@code given type} is uppercase, otherwise keep it as it is
     * @param defaultType Fallback type if {@code given type} is blank or null
     * @param <E>         Type of EnumType
     * @return enum type
     */
    static <E extends EnumType> E factory(String type, @NonNull Class<E> clazz, boolean uppercase, E defaultType) {
        if (Strings.isBlank(type)) {
            return defaultType;
        }
        final String t = Strings.optimizeMultipleSpace(type);
        final String st = uppercase ? t.toUpperCase(Locale.ENGLISH) : t;
        return ReflectionField.streamConstants(clazz)
                              .filter(Objects::nonNull)
                              .filter(et -> et.type().equals(st) ||
                                            Objects.nonNull(et.alternatives()) && et.alternatives().contains(st))
                              .findAny()
                              .orElseGet(
                                  () -> ReflectionClass.createObject(clazz, new Arguments().put(String.class, st)));
    }

    @JsonProperty(value = "type")
    @NonNull String type();

    @JsonProperty(value = "alternatives")
    default Collection<String> alternatives() {
        return null;
    }

    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    abstract class AbstractEnumType implements EnumType {

        @NonNull
        @EqualsAndHashCode.Include
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
