package io.github.zero88.msa.bp.exceptions;

public final class NotFoundException extends BlueprintException {

    public NotFoundException(String message, Throwable e) {
        super(ErrorCode.NOT_FOUND, message, e);
    }

    public NotFoundException(String message) {
        this(message, null);
    }

    public NotFoundException(Throwable e) {
        this(null, e);
    }

}
