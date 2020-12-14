package io.github.zero88.msa.bp.exceptions;

import io.github.zero88.exceptions.ErrorCode;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class BlueprintException extends RuntimeException {

    @Include
    private final ErrorCode errorCode;

    public BlueprintException(ErrorCode errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }

    public BlueprintException(ErrorCode errorCode, String message) { this(errorCode, message, null); }

    public BlueprintException(ErrorCode errorCode, Throwable e)    { this(errorCode, null, e); }

    public BlueprintException(String message, Throwable e)         { this(ErrorCode.UNKNOWN_ERROR, message, e); }

    public BlueprintException(String message)                      { this(message, null); }

    public BlueprintException(Throwable e)                         { this(ErrorCode.UNKNOWN_ERROR, null, e); }

}
