package cloud.playio.qwe.micro.servicetype;

import io.vertx.core.Future;
import cloud.playio.qwe.dto.msg.RequestData;
import cloud.playio.qwe.eventbus.EventAction;
import cloud.playio.qwe.eventbus.EventMessage;

public interface EventMessagePusher {

    /**
     * Push data via Event Bus then consume reply data
     *
     * @param action      Event action
     * @param requestData Request Data
     * @return a future
     */
    Future<EventMessage> execute(EventAction action, RequestData requestData);

}
