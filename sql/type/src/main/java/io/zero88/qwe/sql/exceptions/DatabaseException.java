package io.zero88.qwe.sql.exceptions;

import io.zero88.qwe.exceptions.ErrorCode;
import io.zero88.qwe.exceptions.QWEException;

public class DatabaseException extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("DATABASE_ERROR");

    protected DatabaseException(ErrorCode errorCode, String message, Throwable e) {
        super(errorCode, message, e);
    }

    public DatabaseException(String message, Throwable e) {
        this(CODE, message, e);
    }

    public DatabaseException(String message) { this(message, null); }

    public DatabaseException(Throwable e)    { this(null, e); }

}
