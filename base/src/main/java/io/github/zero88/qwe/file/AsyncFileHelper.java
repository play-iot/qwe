package io.github.zero88.qwe.file;

import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import io.github.zero88.exceptions.FileException;
import io.github.zero88.qwe.exceptions.AlreadyExistException;
import io.github.zero88.qwe.exceptions.CarlException;
import io.github.zero88.qwe.exceptions.ConflictException;
import io.github.zero88.qwe.exceptions.NotFoundException;
import io.github.zero88.qwe.exceptions.SecurityException.InsufficientPermissionError;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.core.file.FileSystemException;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.file.FileSystem;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Async file helper
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AsyncFileHelper {

    @NonNull
    protected final FileSystem fs;

    /**
     * Max file size in Megabytes (MB)
     */
    public abstract int maxSize();

    public static abstract class AsyncFileHelperBuilder<C extends AsyncFileHelper, B extends AsyncFileHelperBuilder<C, B>> {

        public B vertx(@NonNull io.vertx.core.Vertx vertx) {
            return vertx(Vertx.newInstance(vertx));
        }

        public B vertx(@NonNull Vertx vertx) {
            return fs(vertx.fileSystem());
        }

        public B fs(@NonNull io.vertx.core.file.FileSystem fs) {
            return fs(FileSystem.newInstance(fs));
        }

        public B fs(@NonNull FileSystem fs) {
            this.fs = fs;
            return self();
        }

    }

    /**
     * Create recursive folders
     *
     * @param path   a given path
     * @param option a folder permissions
     * @return a reference to path for fluent API
     */
    public Single<Path> mkdirs(@NonNull Path path, @NonNull FileOption option) {
        return fs.rxMkdirs(path.toString(), option.getFolderPerms())
                 .onErrorResumeNext(t -> convertException(t, path).ignoreElement())
                 .andThen(fs.rxChown(path.toString(), option.getOwner(), null)
                            .onErrorResumeNext(t -> convertException(t, path).ignoreElement()))
                 .andThen(Single.just(path));
    }

    /**
     * Create file and its parent folder if doesn't exist
     *
     * @param path   a given path
     * @param option a file option
     * @return a reference to path for fluent API
     * @implNote It is required to verify file first by {@link #verifyFile(Path)} to ensure file is not existed
     */
    public Single<Path> createFile(@NonNull Path path, @NonNull FileOption option) {
        if (!option.isAutoCreate()) {
            return toFE(FileOptionException.disallowCreation());
        }
        return this.mkdirs(path.getParent(), option)
                   .flatMapCompletable(p -> fs.rxCreateFile(path.toString(), option.getFilePerms())
                                              .onErrorResumeNext(t -> convertException(t, path).ignoreElement()))
                   .andThen(fs.rxChown(path.toString(), option.getOwner(), null)
                              .onErrorResumeNext(t -> convertException(t, path).ignoreElement()))
                   .andThen(Single.just(path));
    }

    /**
     * Delete file
     *
     * @param path   a given path
     * @param option a file option
     * @return a reference to path for fluent API
     * @implNote It is required to verify file first by {@link #verifyFile(Path)} to ensure file is existed
     */
    public Single<Path> deleteFile(@NonNull Path path, @NonNull FileOption option) {
        return fs.rxDelete(path.toString())
                 .onErrorResumeNext(t -> convertException(t, path).ignoreElement())
                 .andThen(Single.just(path));
    }

    /**
     * Verify folder must existed
     *
     * @param path given path
     * @return a verified output
     * @see FileVerifiedOutput
     */
    public Single<FileVerifiedOutput> verifyFolder(@NonNull Path path) {
        return verify(path, o -> o.validate(FileValidator.MUST_BE_FOLDER));
    }

    /**
     * Verify file must existed and file's size doesn't exceed {@link #maxSize()} in megabytes
     *
     * @param path given path
     * @return a verified output
     * @see FileVerifiedOutput
     */
    public Single<FileVerifiedOutput> verifyFile(@NonNull Path path) {
        return verify(path, o -> o.validate(FileValidator.MUST_BE_FILE)
                                  .validate(FileValidator.FILE_SIZE_IS_NOT_GREATER_THAN.apply(this.maxSize())));
    }

    protected <T> Single<T> convertException(@NonNull Throwable t, @NonNull Path path) {
        if (t instanceof FileSystemException) {
            Throwable cause = t.getCause();
            if (cause instanceof AccessDeniedException) {
                return toFE(new InsufficientPermissionError("Unable access file '" + path + "'", cause));
            }
            if (cause instanceof NoSuchFileException) {
                return toFE(new NotFoundException("Not found file '" + path + "'", cause));
            }
            if (cause instanceof FileAlreadyExistsException) {
                return toFE(new AlreadyExistException("Already existed file '" + path + "'", cause));
            }
            if (Optional.ofNullable(cause)
                        .flatMap(c -> Optional.ofNullable(c.getMessage()))
                        .map(s -> s.contains("Not a directory"))
                        .orElse(false)) {
                return toFE(new ConflictException("One of item in path '" + path + "' is not a directory"));
            }
            if (Objects.nonNull(cause)) {
                return toFE(cause.getMessage(), cause);
            }
        }
        return toFE(t);
    }

    protected <T, V> Function<V, Single<T>> createDataConverterWrapper(@NonNull Function<V, T> func) {
        return o -> Single.just(func.apply(o)).onErrorResumeNext(t -> toFE("Invalid data format", t));
    }

    protected final <T> Single<T> toFE(@NonNull Throwable t) {
        if (t instanceof CarlException) {
            //TODO Should check message
            return toFE(t.getMessage(), t);
        }
        return Single.error(new FileException(t));
    }

    protected final <T> Single<T> toFE(@NonNull String error) {
        return toFE(error, null);
    }

    protected final <T> Single<T> toFE(@NonNull String error, Throwable t) {
        return Single.error(new FileException(error, t));
    }

    private Single<FileVerifiedOutput> verify(@NonNull Path path,
                                              @NonNull Function<FileVerifiedOutput, FileVerifiedOutput> validator) {
        return fs.rxExists(path.toString())
                 .filter(isExist -> isExist)
                 .flatMap(b -> fs.rxProps(path.toString()).toMaybe())
                 .map(props -> FileVerifiedOutput.existed(path, props))
                 .map(validator)
                 .defaultIfEmpty(FileVerifiedOutput.notExisted(path))
                 .toSingle();
    }

}
