package cloud.playio.qwe;

import cloud.playio.qwe.dto.ErrorData;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EBContract;
import cloud.playio.qwe.eventbus.EventListener;
import cloud.playio.qwe.eventbus.EventPattern;

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
