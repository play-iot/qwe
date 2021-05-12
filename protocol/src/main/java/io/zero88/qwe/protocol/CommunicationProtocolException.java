package io.zero88.qwe.protocol;

import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.ErrorCode;

public final class CommunicationProtocolException extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("COMMUNICATION_PROTOCOL_ERROR");

    public CommunicationProtocolException(String message, Throwable e) {
        super(CommunicationProtocolException.CODE, message, e);
    }

    public CommunicationProtocolException(String message) { this(message, null); }

    public CommunicationProtocolException(Throwable e)    { this(null, e); }

}
