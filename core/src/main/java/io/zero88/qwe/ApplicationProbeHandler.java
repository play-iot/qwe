package io.zero88.qwe;

import io.zero88.qwe.dto.ErrorData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EventContractor;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.event.EventPattern;

import lombok.NonNull;

/**
 * Application probe handler
 * <p>
 * It is handler by pattern {@link EventPattern#PUBLISH_SUBSCRIBE}
 */
public interface ApplicationProbeHandler extends EventListener {

    @EventContractor(action = "NOTIFY", returnType = boolean.class)
    boolean success(@NonNull RequestData requestData);

    @EventContractor(action = "NOTIFY_ERROR", returnType = boolean.class)
    boolean error(@NonNull ErrorData error);

    /**
     * Application readiness handler
     *
     * @see ApplicationProbe#readiness()
     */
    interface ApplicationReadinessHandler extends ApplicationProbeHandler {}


    /**
     * Application liveness handler
     *
     * @see ApplicationProbe#liveness()
     */
    interface ApplicationLivenessHandler extends ApplicationProbeHandler {}

}
