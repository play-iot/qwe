package io.github.zero88.msa.blueprint.exceptions;

public final class CommunicationProtocolException extends BlueprintException {

    public CommunicationProtocolException(String message, Throwable e) {
        super(ErrorCode.COMMUNICATION_PROTOCOL_ERROR, message, e);
    }

    public CommunicationProtocolException(String message) { this(message, null); }

    public CommunicationProtocolException(Throwable e)    { this(null, e); }

}
