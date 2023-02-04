package cloud.playio.qwe.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.core.Vertx;
import cloud.playio.qwe.SharedDataLocalProxy;
import cloud.playio.qwe.auth.UserInfo;

/**
 * This annotation is used to inject {@code EventBus context} into method parameter.
 * <p>
 * The context is one of {@code EventAction}, {@code Vert.x}, {@code SharedDataLocalProxy}, {@code EventBusClient} and
 * {@code UserInfo}
 *
 * @see EventAction
 * @see EventBusClient
 * @see SharedDataLocalProxy
 * @see UserInfo
 * @see Vertx
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EBContext {}
