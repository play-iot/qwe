package io.github.zero88.msa.blueprint.exceptions;

public final class NetworkException extends BlueprintException {

    public NetworkException(String message, Throwable e) { super(ErrorCode.NETWORK_ERROR, message, e); }

    public NetworkException(String message)              { this(message, null); }

    public NetworkException(Throwable e)                 { this(null, e); }

}
