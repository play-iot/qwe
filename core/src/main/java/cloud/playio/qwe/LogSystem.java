package cloud.playio.qwe;

import lombok.NonNull;

@FunctionalInterface
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
