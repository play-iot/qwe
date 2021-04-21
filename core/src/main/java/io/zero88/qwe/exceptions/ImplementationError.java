package io.zero88.qwe.exceptions;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.exceptions.HiddenException;

import lombok.NonNull;

public class ImplementationError extends HiddenException {

    public ImplementationError(@NonNull ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

    public ImplementationError(@NonNull ErrorCode code, @NonNull String message) {
        this(code, message, null);
    }

}
