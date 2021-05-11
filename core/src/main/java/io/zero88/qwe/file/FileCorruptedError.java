package io.zero88.qwe.file;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.exceptions.CarlException;
import io.zero88.qwe.exceptions.ErrorCode;

public final class FileCorruptedError extends CarlException {

    public static final ErrorCode FILE_CORRUPTED = ErrorCode.parse("FILE_CORRUPTED");

    public FileCorruptedError(String message, Throwable e) {
        super(FILE_CORRUPTED, Strings.fallback(message, "File corrupted"), e);
    }

    public FileCorruptedError(String message) {
        this(message, null);
    }

    public FileCorruptedError(Throwable e) {
        this(null, e);
    }

}
