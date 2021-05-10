package io.zero88.qwe.exceptions;

public final class DataBeingUsedException extends CarlException {

    public DataBeingUsedException(String message, Throwable e) {
        super(ErrorCode.DATA_BEING_USED, message, e);
    }

    public DataBeingUsedException(String message) {
        this(message, null);
    }

    public DataBeingUsedException(Throwable e) {
        this("Data is being used", e);
    }

}
