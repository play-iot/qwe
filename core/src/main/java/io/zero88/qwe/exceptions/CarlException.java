package io.zero88.qwe.exceptions;

import io.github.zero88.exceptions.ErrorCode;
import io.github.zero88.exceptions.ErrorCodeException;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class CarlException extends RuntimeException implements ErrorCodeException {

    @Include
    private final ErrorCode errorCode;

    public CarlException(ErrorCode errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }

    public CarlException(ErrorCode errorCode, String message) { this(errorCode, message, null); }

    public CarlException(ErrorCode errorCode, Throwable e)    { this(errorCode, null, e); }

    public CarlException(String message, Throwable e)         { this(ErrorCode.UNKNOWN_ERROR, message, e); }

    public CarlException(String message)                      { this(message, null); }

    public CarlException(Throwable e)                         { this(ErrorCode.UNKNOWN_ERROR, null, e); }

}
