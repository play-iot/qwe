package io.github.zero88.msa.bp.micro.metadata;

import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.ServiceException;

public final class ServiceNotFoundException extends ServiceException {

    public static final ErrorCode CODE = ErrorCode.parse("SERVICE_NOT_FOUND");

    public ServiceNotFoundException(String message, Throwable e) {
        super(CODE, message, e);
    }

    public ServiceNotFoundException(String message) {
        this(message, null);
    }

    public ServiceNotFoundException(Throwable e) {
        this(null, e);
    }

}
