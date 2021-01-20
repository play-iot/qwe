package io.github.zero88.qwe.exceptions;

public class ServiceException extends CarlException {

    public ServiceException(String message, Throwable e) {
        this(ErrorCode.SERVICE_ERROR, message, e);
    }

    public ServiceException(String message) { this(message, null); }

    public ServiceException(Throwable e)    { this(null, e); }

    protected ServiceException(ErrorCode code, String message, Throwable e) {
        super(code, message, e);
    }

}
