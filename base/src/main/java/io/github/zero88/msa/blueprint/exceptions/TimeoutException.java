package io.github.zero88.msa.blueprint.exceptions;

public final class TimeoutException extends BlueprintException {

    public TimeoutException(String message, Throwable e) { super(ErrorCode.TIMEOUT_ERROR, message, e); }

    public TimeoutException(String message)              { this(message, null); }

}
