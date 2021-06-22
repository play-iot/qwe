package io.zero88.qwe.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PriorityUtils {

    public static final int PRIORITY_FACTOR = 100;

    public static int priorityOrder(int len) {
        return priorityOrder(len, PRIORITY_FACTOR);
    }

    public static int priorityOrder(int len, int factor) {
        return len > factor ? priorityOrder(len, factor * 10) : (factor - len) * factor;
    }

}
