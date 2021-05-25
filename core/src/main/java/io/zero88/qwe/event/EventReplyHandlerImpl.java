package io.zero88.qwe.event;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Strings;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.exceptions.TimeoutException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("rawtypes")
class EventReplyHandlerImpl implements EventReplyHandler {

    private final String system;
    private String address;
    private EventAction action;

    EventReplyHandlerImpl(String system) {
        this.system = system;
    }

    EventReplyHandlerImpl() {
        this(EventReplyHandler.DEFAULT_SYSTEM);
    }

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
        final String msg = Strings.format("No response on EventAction [{0}] from address [{1}]", action, address);
        return EventMessage.replyError(action, new TimeoutException(msg, new HiddenException(err)));
    }

    public String replySystem() {
        return system;
    }

}
