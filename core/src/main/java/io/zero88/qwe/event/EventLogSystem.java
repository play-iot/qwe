package io.zero88.qwe.event;

import io.zero88.qwe.LogSystem;

import lombok.NonNull;

public interface EventLogSystem extends LogSystem {

    interface EventListenerLogSystem extends EventLogSystem {

        @Override
        default @NonNull String function() {
            return "EBListener";
        }

    }


    interface EventReplyLogSystem extends EventLogSystem {

        @Override
        default @NonNull String function() {
            return "EBReply";
        }

    }

}
