package io.github.zero88.msa.bp.exceptions;

public final class AlreadyExistException extends BlueprintException {

    public static final ErrorCode CODE = ErrorCode.ALREADY_EXIST;

    public AlreadyExistException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public AlreadyExistException(String message) {
        this(message, null);
    }

    public AlreadyExistException(Throwable e) {
        this(null, e);
    }

}
