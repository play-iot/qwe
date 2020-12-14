package io.github.zero88.msa.blueprint.exceptions;

public final class BeingUsedException extends BlueprintException {

    public BeingUsedException(String message, Throwable e) {
        super(ErrorCode.BEING_USED, message, e);
    }

    public BeingUsedException(String message) {
        this(message, null);
    }

    public BeingUsedException(Throwable e) {
        this(null, e);
    }

}
