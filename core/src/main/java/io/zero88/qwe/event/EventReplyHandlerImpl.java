package io.zero88.qwe.event;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Strings;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.dto.ErrorMessage;
import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.ServiceUnavailable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("rawtypes")
class EventReplyHandlerImpl implements EventReplyHandler {

    private String address;
    private EventAction action;

    @Override
    public EventReplyHandler loadContext(String address, EventAction action) {
        this.address = address;
        this.action = action;
        return this;
    }

    @Override
    public Message from(EventMessage eventMessage) {
        return null;
    }

    @Override
    public EventMessage to(Message objectMessage) {
        @SuppressWarnings("unchecked")
        final EventMessage msg = EventMessage.convert(objectMessage);
        log.info("{}::Backend EventBus response | Address: {} | Action: {} | Status: {}", replySystem(), address,
                 msg.getAction(), msg.getStatus());
        return msg;
    }

    public EventMessage otherwise(Throwable err) {
        String msg = Strings.format("No reply from action [{0}] from [{1}]", action, address);
        ServiceUnavailable ex = new ServiceUnavailable("Service unavailable",
                                                       new HiddenException(ErrorCode.EVENT_ERROR, msg, err));
        return EventMessage.error(EventAction.REPLY, action, ErrorMessage.parse(ex));
    }

}
