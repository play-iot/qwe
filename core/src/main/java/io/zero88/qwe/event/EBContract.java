package io.zero88.qwe.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks class method to handle event type.
 *
 * @see EBContext
 * @see EBParam
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EBContract {

    /**
     * @return the possible event types that a method can process
     * @see EventAction
     */
    String[] action();

}
