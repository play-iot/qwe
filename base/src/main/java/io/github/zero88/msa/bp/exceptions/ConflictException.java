package io.github.zero88.msa.bp.exceptions;

public final class ConflictException extends BlueprintException {

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
