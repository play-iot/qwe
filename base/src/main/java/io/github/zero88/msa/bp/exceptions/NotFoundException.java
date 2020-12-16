package io.github.zero88.msa.bp.exceptions;

public final class NotFoundException extends BlueprintException {

    public static final ErrorCode CODE = ErrorCode.NOT_FOUND;

    public NotFoundException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public NotFoundException(String message) {
        this(message, null);
    }

    public NotFoundException(Throwable e) {
        this(null, e);
    }

}
