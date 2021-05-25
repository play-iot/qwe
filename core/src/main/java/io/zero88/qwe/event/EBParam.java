package io.zero88.qwe.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define EventBus parameter name
 *
 * @apiNote Able to omit if {@code Java method} has only one parameter that can be deserialize from json
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface EBParam {

    String value();

}
