package cloud.playio.qwe.eventbus;

import cloud.playio.qwe.LogSystem;

import lombok.NonNull;

public interface EventBusLogSystem extends LogSystem {

    interface EventListenerLogSystem extends EventBusLogSystem {

        @Override
        default @NonNull String function() {
            return "EBListener";
        }

    }


    interface EventReplyLogSystem extends EventBusLogSystem {

        @Override
        default @NonNull String function() {
            return "EBReply";
        }

    }

}
