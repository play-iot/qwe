package io.zero88.qwe;

import io.zero88.qwe.dto.ErrorData;
import io.zero88.qwe.dto.msg.RequestData;
import io.zero88.qwe.event.EBContract;
import io.zero88.qwe.event.EventListener;
import io.zero88.qwe.event.EventPattern;

import lombok.NonNull;

/**
 * Application probe handler
 * <p>
 * It is handler by pattern {@link EventPattern#PUBLISH_SUBSCRIBE}
 */
public interface ApplicationProbeHandler extends EventListener {

    @EBContract(action = "NOTIFY")
    boolean success(@NonNull RequestData requestData);

    @EBContract(action = "NOTIFY_ERROR")
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
