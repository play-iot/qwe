package io.zero88.qwe.event;

import io.github.zero88.exceptions.HiddenException;
import io.github.zero88.utils.Strings;
import io.vertx.core.eventbus.Message;
import io.zero88.qwe.exceptions.TimeoutException;

@SuppressWarnings({"rawtypes", "unchecked"})
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
    public EventMessage to(Message objectMessage) {
        final EventMessage msg = EventMessage.convert(objectMessage);
        logger().info(decor("Response [{}][{}=>{}][{}]"), address, msg.getAction(), msg.getPrevAction(),
                      msg.getStatus());
        return msg;
    }

    public EventMessage otherwise(Throwable error) {
        final String msg = Strings.format("No response [{0}][{1}]", address, action);
        return EventMessage.replyError(action, new TimeoutException(msg, new HiddenException(error)));
    }

}
