package io.zero88.qwe.exceptions;

public final class UnsupportedException extends CarlException {

    public UnsupportedException(String message, Throwable e) {
        super(ErrorCode.UNSUPPORTED, message, e);
    }

    public UnsupportedException(String message) { this(message, null); }

    public UnsupportedException(Throwable e)    { this(null, e); }

}
