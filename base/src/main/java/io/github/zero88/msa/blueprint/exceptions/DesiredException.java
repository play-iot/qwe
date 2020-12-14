package io.github.zero88.msa.blueprint.exceptions;

public final class DesiredException extends BlueprintException {

    public DesiredException(String message, Throwable e) {
        super(ErrorCode.DESIRED_ERROR, message, e);
    }

    public DesiredException(String message) {
        super(ErrorCode.DESIRED_ERROR, message);
    }

    public DesiredException(Throwable e) {
        super(ErrorCode.DESIRED_ERROR, e);
    }

}
