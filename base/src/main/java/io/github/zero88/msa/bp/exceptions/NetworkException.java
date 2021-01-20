package io.github.zero88.msa.bp.exceptions;

public final class NetworkException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("NETWORK_ERROR");

    public NetworkException(String message, Throwable e) { super(CODE, message, e); }

    public NetworkException(String message)              { this(message, null); }

    public NetworkException(Throwable e)                 { this(null, e); }

}
