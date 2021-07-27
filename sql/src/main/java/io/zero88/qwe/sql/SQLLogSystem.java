package io.zero88.qwe.sql;

import io.zero88.qwe.HasLogger;
import io.zero88.qwe.LogSystem;

import lombok.NonNull;

public interface SQLLogSystem extends LogSystem, HasLogger {

    @Override
    default @NonNull String function() {
        return "SQL";
    }

}
