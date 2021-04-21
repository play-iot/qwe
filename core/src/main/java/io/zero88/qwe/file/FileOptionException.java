package io.zero88.qwe.file;

import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.ErrorCode;

public class FileOptionException extends CarlException {

    public static final ErrorCode CODE = ErrorCode.parse("FILE_OPTION_ERROR");

    public FileOptionException(String message) {
        super(CODE, message);
    }

    public static FileOptionException disallowCreation() {
        return new FileOptionException("Disallow creating file. Need to enable an auto-create option");
    }

    public static FileOptionException disallowOverwrite() {
        return new FileOptionException("Disallow overwriting file. Need to enable an overwrite option");
    }

}
