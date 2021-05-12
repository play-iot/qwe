package io.zero88.qwe.exceptions;

import io.github.zero88.utils.Strings;

public final class ServiceNotFoundException extends ServiceException {

    public ServiceNotFoundException(String message, Throwable e) {
        super(ErrorCode.SERVICE_NOT_FOUND, Strings.fallback(message, "Service not found"), e);
    }

    public ServiceNotFoundException(String message) {
        this(message, null);
    }

    public ServiceNotFoundException(Throwable e) {
        this(null, e);
    }

    public ServiceNotFoundException() {
        this((String) null);
    }

}
