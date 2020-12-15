package io.github.zero88.msa.bp.micro.metadata;

import io.github.zero88.msa.bp.exceptions.ErrorCode;
import io.github.zero88.msa.bp.exceptions.ServiceException;

public final class ServiceNotFoundException extends ServiceException {

    public ServiceNotFoundException(String message, Throwable e) {
        super(ErrorCode.SERVICE_NOT_FOUND, message, e);
    }

    public ServiceNotFoundException(String message) {
        this(message, null);
    }

    public ServiceNotFoundException(Throwable e) {
        this(null, e);
    }

}