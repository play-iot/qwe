package io.zero88.qwe.file;

import java.nio.file.Path;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.exceptions.QWEException;
import io.zero88.qwe.exceptions.ErrorCode;

public final class FileAlreadyExistError extends QWEException {

    public static final ErrorCode CODE = ErrorCode.parse("FILE_ALREADY_EXISTS");

    public FileAlreadyExistError(String message, Throwable e) {
        super(CODE, Strings.fallback(message, "File is already existed"), e);
    }

    public FileAlreadyExistError(Path path, Throwable cause) {
        this("Already existed file [" + path + "]", cause);
    }

}
