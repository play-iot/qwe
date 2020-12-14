package io.github.zero88.msa.bp.exceptions;

public final class StateException extends BlueprintException {

    public StateException(String message, Throwable e) {
        super(ErrorCode.STATE_ERROR, message, e);
    }

    public StateException(String message) { this(message, null); }

    public StateException(Throwable e)    { this(null, e); }

}
