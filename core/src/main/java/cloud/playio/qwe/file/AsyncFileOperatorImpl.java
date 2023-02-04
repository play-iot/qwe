package cloud.playio.qwe.file;

import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import io.github.zero88.exceptions.FileException;
import io.github.zero88.utils.Strings;
import io.vertx.core.Future;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.FileSystemException;
import cloud.playio.qwe.exceptions.QWEException;
import cloud.playio.qwe.exceptions.ConflictException;
import cloud.playio.qwe.exceptions.SecurityException.InsufficientPermissionError;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
class AsyncFileOperatorImpl implements AsyncFileOperator {

    @NonNull
    protected final FileSystem fs;
    protected final int maxSize;


    public static abstract class AsyncFileOperatorImplBuilder<C extends AsyncFileOperator,
                                                                 B extends AsyncFileOperatorImplBuilder<C, B>> {

        public B vertx(@NonNull io.vertx.core.Vertx vertx) {
            return fs(vertx.fileSystem());
        }

        public B fs(@NonNull FileSystem fs) {
            this.fs = fs;
            return self();
        }

    }

    @Override
    public Future<Path> mkdirs(@NonNull Path path, @NonNull FileOption option) {
        return fs.mkdirs(path.toString(), option.getFolderPerms())
                 .recover(t -> convertException(t, path))
                 .flatMap(unused -> fs.chown(path.toString(), option.getOwner(), null)
                                      .recover(t -> convertException(t, path)))
                 .map(unused -> path);
    }

    @Override
    public Future<Path> touch(@NonNull Path path, @NonNull FileOption option) {
        if (!option.isAutoCreate()) {
            return Future.failedFuture(FileOptionException.disallowCreation());
        }
        return this.mkdirs(path.getParent(), option)
                   .flatMap(p -> fs.createFile(path.toString(), option.getFilePerms())
                                   .recover(t -> convertException(t, path)))
                   .flatMap(unused -> fs.chown(path.toString(), option.getOwner(), null)
                                        .recover(t -> convertException(t, path)))
                   .map(unused -> path);
    }

    @Override
    public Future<Path> deleteFile(@NonNull Path path, @NonNull FileOption option) {
        return fs.delete(path.toString()).recover(t -> convertException(t, path)).map(unused -> path);
    }

    protected <T> Future<T> convertException(@NonNull Throwable t, @NonNull Path path) {
        if (t instanceof FileSystemException) {
            Throwable cause = t.getCause();
            if (cause instanceof AccessDeniedException) {
                return toFE(new InsufficientPermissionError("Unable access file [" + path + "]", cause));
            }
            if (cause instanceof NoSuchFileException) {
                return Future.failedFuture(new FileNotFoundError(path, t));
            }
            if (cause instanceof FileAlreadyExistsException) {
                return Future.failedFuture(new FileAlreadyExistError(path, cause));
            }
            if (Optional.ofNullable(cause)
                        .flatMap(c -> Optional.ofNullable(c.getMessage()))
                        .map(s -> s.contains("Not a directory"))
                        .orElse(false)) {
                return toFE(new ConflictException("One of item in path [" + path + "] is not a directory"));
            }
            if (Objects.nonNull(cause)) {
                return toFE(Strings.fallback(cause.getMessage(), cause.toString()), cause);
            }
        }
        return toFE(t);
    }

    protected final <T> Future<T> toFE(@NonNull Throwable t) {
        if (t instanceof QWEException) {
            //TODO Should check message
            return toFE(t.getMessage(), t);
        }
        return Future.failedFuture(new FileException(t));
    }

    protected final <T> Future<T> toFE(@NonNull String error, Throwable t) {
        return Future.failedFuture(new FileException(error, t));
    }

}
