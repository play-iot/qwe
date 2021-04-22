package io.zero88.qwe.file;

import java.nio.file.Path;
import java.util.function.Function;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.file.FileSystem;

import lombok.NonNull;

@VertxGen
public interface AsyncFileOperator {

    /**
     * Get file system
     *
     * @return file system
     */
    FileSystem fs();

    /**
     * Max file size in Megabytes (MB)
     *
     * @return max file size
     */
    int maxSize();

    /**
     * Create recursive folders
     *
     * @param path   a given path
     * @param option a folder permissions
     * @return a reference to path for fluent API
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Path> mkdirs(@NonNull Path path, @NonNull FileOption option);

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default AsyncFileOperator mkdirs(Path path, @NonNull FileOption option, Handler<AsyncResult<Path>> handler) {
        mkdirs(path, option).onComplete(handler);
        return this;
    }

    /**
     * Create file and its parent folder if doesn't exist
     *
     * @param path   a given path
     * @param option a file option
     * @return a reference to path for fluent API
     * @implNote It is required to verify file first by {@link #verifyFile(Path)} to ensure file is not existed
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Path> touch(@NonNull Path path, @NonNull FileOption option);

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default AsyncFileOperator touch(Path path, @NonNull FileOption option, Handler<AsyncResult<Path>> handler) {
        touch(path, option).onComplete(handler);
        return this;
    }

    /**
     * Delete file
     *
     * @param path   a given path
     * @param option a file option
     * @return a reference to path for fluent API
     * @implNote It is required to verify file first by {@link #verifyFile(Path)} to ensure file is existed
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Path> deleteFile(@NonNull Path path, @NonNull FileOption option);

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default AsyncFileOperator deleteFile(Path path, @NonNull FileOption option, Handler<AsyncResult<Path>> handler) {
        deleteFile(path, option).onComplete(handler);
        return this;
    }

    /**
     * Verify folder must existed
     *
     * @param path given path
     * @return a verified output
     * @see FileVerifiedOutput
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<FileVerifiedOutput> verifyFolder(@NonNull Path path) {
        return verify(path, o -> o.validate(FileValidator.MUST_BE_FOLDER));
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default AsyncFileOperator verifyFolder(Path path, Handler<AsyncResult<FileVerifiedOutput>> handler) {
        verifyFolder(path).onComplete(handler);
        return this;
    }

    /**
     * Verify file must existed and file's size doesn't exceed {@link #maxSize()} in megabytes
     *
     * @param path given path
     * @return a verified output
     * @see FileVerifiedOutput
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<FileVerifiedOutput> verifyFile(@NonNull Path path) {
        return verify(path, o -> o.validate(FileValidator.MUST_BE_FILE)
                                  .validate(FileValidator.FILE_SIZE_IS_NOT_GREATER_THAN.apply(this.maxSize())));
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default AsyncFileOperator verifyFile(Path path, Handler<AsyncResult<FileVerifiedOutput>> handler) {
        verifyFile(path).onComplete(handler);
        return this;
    }

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<FileVerifiedOutput> verify(@NonNull Path path,
                                              @NonNull Function<FileVerifiedOutput, FileVerifiedOutput> validator) {
        return fs().exists(path.toString()).flatMap(isExist -> {
            if (isExist) {
                return fs().props(path.toString()).map(props -> FileVerifiedOutput.existed(path, props)).map(validator);
            }
            return Future.succeededFuture(FileVerifiedOutput.notExisted(path));
        });
    }

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default AsyncFileOperator verify(@NonNull Path path,
                                     @NonNull Function<FileVerifiedOutput, FileVerifiedOutput> validator,
                                     Handler<AsyncResult<FileVerifiedOutput>> handler) {
        verify(path, validator).onComplete(handler);
        return this;
    }

}
