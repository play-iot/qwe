package io.github.zero88.qwe.scheduler.core.impl;

import io.github.zero88.qwe.scheduler.core.TaskExecutionContext;

public interface TaskExecutionContextInternal extends TaskExecutionContext {

    void internalComplete();

    Object data();

    Throwable error();

}
