package io.github.zero88.msa.bp.exceptions;

public final class BeingUsedException extends BlueprintException {

    public static final ErrorCode CODE = ErrorCode.parse("BEING_USED");

    public BeingUsedException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public BeingUsedException(String message) {
        this(message, null);
    }

    public BeingUsedException(Throwable e) {
        this(null, e);
    }

}
