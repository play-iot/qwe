package io.zero88.qwe.sql.exceptions;

import io.zero88.qwe.exceptions.ErrorCode;

public class DBConverterException extends DatabaseException {

    public static final ErrorCode DB_CONVERTER_ERROR = ErrorCode.parse("DB_CONVERTER_ERROR");

    protected DBConverterException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public DBConverterException(String message, Throwable e) {
        this(DB_CONVERTER_ERROR, message, e);
    }

    public DBConverterException(String message) {
        this(message, null);
    }

    public DBConverterException(Throwable e) {
        this(null, e);
    }

}
