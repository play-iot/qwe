package io.github.zero88.msa.bp.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Marks class method to handle event type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventContractor {

    /**
     * @return the possible event types that a method can process
     * @see EventAction
     */
    String[] action();

    /**
     * @return Output type of method. Default: {@link JsonObject}
     */
    @NonNull Class<?> returnType() default JsonObject.class;

    /**
     * Define parameter name
     *
     * @apiNote Able to omit if {@code Java method} has only one parameter that can be deserialize from json
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Param {

        String value();

    }

}
