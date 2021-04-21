package io.zero88.qwe.exceptions;

public final class ConflictException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("CONFLICT_ERROR");

    public ConflictException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public ConflictException(String message) {
        this(message, null);
    }

    public ConflictException(Throwable e) {
        this(null, e);
    }

}
