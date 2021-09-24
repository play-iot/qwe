package io.zero88.qwe.eventbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.zero88.qwe.dto.msg.DataTransferObject.StandardKey;
import io.zero88.qwe.dto.msg.RequestData;

/**
 * Binds the value of an {@link EventMessage#getData()} to a resource method parameter
 *
 * @apiNote Able to omit if {@code Java method} has only one parameter that can be deserialized from json
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface EBParam {

    /**
     * Defines the name of the message data whose value will be used to initialize the value of the annotated method
     * argument.
     *
     * @implNote The name value can be one of constant values in {@link StandardKey} if {@code EventMessage data} is
     *     in the standard form as {@link RequestData}
     */
    String value();

}
