package cloud.playio.qwe.sql;

import cloud.playio.qwe.HasLogger;
import cloud.playio.qwe.LogSystem;

import lombok.NonNull;

public interface SQLLogSystem extends LogSystem, HasLogger {

    @Override
    default @NonNull String function() {
        return "SQL";
    }

}
