package cloud.playio.qwe.file;

import java.nio.file.Path;
import java.util.function.Function;

import io.github.zero88.exceptions.FileException;
import io.vertx.core.file.FileProps;

import lombok.NonNull;

@FunctionalInterface
public interface FileValidator {

    void validate(@NonNull Path path, @NonNull FileProps props) throws FileException;

    FileValidator MUST_BE_SYMLINK = (path, props) -> {
        if (!props.isSymbolicLink()) {
            throw new FileException("Given path [" + path + "] is not a symlink");
        }
    };

    FileValidator MUST_BE_FILE = (path, props) -> {
        if (!props.isRegularFile()) {
            throw new FileException("Given path [" + path + "] is not a file");
        }
    };

    FileValidator MUST_BE_FOLDER = (path, props) -> {
        if (!props.isDirectory()) {
            throw new FileException("Given path [" + path + "] is not a folder");
        }
    };

    Function<Integer, FileValidator> FILE_SIZE_IS_NOT_GREATER_THAN = size -> (path, props) -> {
        if (!props.isRegularFile()) {
            return;
        }
        if (size < (props.size() / 1024 / 1024)) {
            throw new FileException("File[" + path + "] exceeds " + size + "MB");
        }
    };

}
