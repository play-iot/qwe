package io.zero88.qwe.event;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Strings;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.exceptions.TimeoutException;

@SuppressWarnings({"rawtypes", "unchecked"})
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
    public EventMessage to(Message objectMessage) {
        final EventMessage msg = EventMessage.convert(objectMessage);
        logger().info("{}::Response [{}][{}=>{}][{}]", replySystem(), address, msg.getAction(), msg.getPrevAction(),
                      msg.getStatus());
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
