package io.github.zero88.qwe.scheduler.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskResultImpl implements TaskResult {

    private final long round;
    private final boolean completed;
    private final Throwable error;
    private final Object data;

}
