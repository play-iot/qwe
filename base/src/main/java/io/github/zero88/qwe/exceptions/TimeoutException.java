package io.github.zero88.qwe.exceptions;

public final class TimeoutException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("TIMEOUT_ERROR");

    public TimeoutException(String message, Throwable e) { super(CODE, message, e); }

    public TimeoutException(String message)              { this(message, null); }

}
