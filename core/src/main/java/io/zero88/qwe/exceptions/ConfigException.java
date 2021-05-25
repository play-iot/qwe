package io.zero88.qwe.exceptions;

import io.github.zero88.utils.Strings;

public class ConfigException extends InitializerError {

    public ConfigException(String message, Throwable e) {
        super(ErrorCode.parse("CONFIG_ERROR"), Strings.fallback(message, "Invalid configuration format"), e);
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(Throwable e) {
        super(e);
    }

}
