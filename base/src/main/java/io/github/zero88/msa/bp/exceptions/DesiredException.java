package io.github.zero88.msa.bp.exceptions;

public final class DesiredException extends CarlException {

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
