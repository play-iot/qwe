package io.zero88.qwe.exceptions;

public final class TimeoutException extends QWEException {

    public TimeoutException(String message, Throwable e) { super(ErrorCode.TIMEOUT_ERROR, message, e); }

    public TimeoutException(String message)              { this(message, null); }

}
