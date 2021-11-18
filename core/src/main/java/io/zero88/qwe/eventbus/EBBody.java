package io.zero88.qwe.eventbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.zero88.qwe.dto.msg.RequestData;

/**
 * Binds the value(s) of a standard {@code EventMessage} data body as {@link RequestData#body()} in to a resource method
 * parameter
 *
 * @implNote If no using standard message structure, then this annotation will be treated as {@link EBParam}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface EBBody {

    /**
     * Defines the name of the {@code message data body} whose value will be used to initialize the value of the
     * annotated method argument
     *
     * @apiNote Default value is {@code blank} means whole {@link RequestData#body()}
     */
    String value() default "";

}
