package io.zero88.qwe.exceptions;

public final class AlreadyExistException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.ALREADY_EXIST;

    public AlreadyExistException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public AlreadyExistException(String message) {
        this(message, null);
    }

    public AlreadyExistException(Throwable e) {
        this(null, e);
    }

}
