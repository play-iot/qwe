package io.github.zero88.msa.bp.exceptions;

import java.io.Serializable;

import io.github.zero88.msa.bp.dto.EnumType;
import io.github.zero88.msa.bp.dto.EnumType.AbstractEnumType;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ErrorCode extends AbstractEnumType
    implements io.github.zero88.exceptions.ErrorCode, EnumType, Serializable {

    public static final ErrorCode CONFLICT_ERROR = new ErrorCode("CONFLICT_ERROR");
    public static final ErrorCode DESIRED_ERROR = new ErrorCode("DESIRED_ERROR");
    public static final ErrorCode INVALID_ARGUMENT = new ErrorCode("INVALID_ARGUMENT");
    public static final ErrorCode ALREADY_EXIST = new ErrorCode("ALREADY_EXIST");
    public static final ErrorCode NOT_FOUND = new ErrorCode("NOT_FOUND");
    public static final ErrorCode SECURITY_ERROR = new ErrorCode("SECURITY_ERROR");
    public static final ErrorCode AUTHENTICATION_ERROR = new ErrorCode("AUTHENTICATION_ERROR");
    public static final ErrorCode INSUFFICIENT_PERMISSION_ERROR = new ErrorCode("INSUFFICIENT_PERMISSION_ERROR");
    public static final ErrorCode HTTP_ERROR = new ErrorCode("HTTP_ERROR");
    public static final ErrorCode SERVICE_ERROR = new ErrorCode("SERVICE_ERROR");
    public static final ErrorCode INITIALIZER_ERROR = new ErrorCode("INITIALIZER_ERROR");
    public static final ErrorCode ENGINE_ERROR = new ErrorCode("ENGINE_ERROR");
    public static final ErrorCode CLUSTER_ERROR = new ErrorCode("CLUSTER_ERROR");
    public static final ErrorCode EVENT_ERROR = new ErrorCode("EVENT_ERROR");
    public static final ErrorCode DATABASE_ERROR = new ErrorCode("DATABASE_ERROR");
    public static final ErrorCode STATE_ERROR = new ErrorCode("STATE_ERROR");
    public static final ErrorCode TIMEOUT_ERROR = new ErrorCode("TIMEOUT_ERROR");
    public static final ErrorCode NETWORK_ERROR = new ErrorCode("NETWORK_ERROR");
    public static final ErrorCode COMMUNICATION_PROTOCOL_ERROR = new ErrorCode("COMMUNICATION_PROTOCOL_ERROR");
    public static final ErrorCode BEING_USED = new ErrorCode("BEING_USED");
    public static final ErrorCode SERVICE_NOT_FOUND = new ErrorCode("SERVICE_NOT_FOUND");

    public ErrorCode(String code) {
        super(code);
    }

    @Override
    public @NonNull String code() {
        return type();
    }

    public static ErrorCode parse(String code) {
        return EnumType.factory(code, ErrorCode.class);
    }

}
