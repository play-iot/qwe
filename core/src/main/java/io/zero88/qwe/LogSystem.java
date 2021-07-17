package io.zero88.qwe;

import lombok.NonNull;

public interface LogSystem {

    /**
     * Defines System function name
     *
     * @return function name
     */
    @NonNull String function();

    /**
     * Decor log with prefix system
     *
     * @param log log
     * @return a decorator log
     */
    default @NonNull String decor(String log) {
        return function() + "::" + log;
    }

}
