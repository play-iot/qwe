package io.github.zero88.msa.bp.exceptions;

public final class CommunicationProtocolException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("COMMUNICATION_PROTOCOL_ERROR");

    public CommunicationProtocolException(String message, Throwable e) {
        super(CommunicationProtocolException.CODE, message, e);
    }

    public CommunicationProtocolException(String message) { this(message, null); }

    public CommunicationProtocolException(Throwable e)    { this(null, e); }

}
