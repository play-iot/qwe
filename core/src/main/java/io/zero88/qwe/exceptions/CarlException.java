package io.zero88.qwe.exceptions;

import io.github.zero88.exceptions.ErrorCodeException;
import io.github.zero88.exceptions.SneakyErrorCodeException;

public class CarlException extends SneakyErrorCodeException implements ErrorCodeException {

    public CarlException(io.github.zero88.exceptions.ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public CarlException(io.github.zero88.exceptions.ErrorCode errorCode, String message) { super(errorCode, message); }

    public CarlException(io.github.zero88.exceptions.ErrorCode errorCode, Throwable e)    { super(errorCode, e); }

    public CarlException(io.github.zero88.exceptions.ErrorCode errorCode)    { super(errorCode); }

    public CarlException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public CarlException(ErrorCode errorCode, String message) { super(errorCode, message); }

    public CarlException(ErrorCode errorCode, Throwable e)    { super(errorCode, e); }

    public CarlException(String message, Throwable e)         { super(message, e); }

    public CarlException(String message)                      { super(message); }

    public CarlException(Throwable e)                         { super(e); }

}
