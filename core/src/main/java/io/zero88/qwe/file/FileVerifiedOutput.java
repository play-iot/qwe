package io.zero88.qwe.file;

import java.nio.file.Path;

import io.vertx.core.file.FileProps;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "Builder")
public class FileVerifiedOutput {

    private final Path path;
    @Default
    private final boolean existed = true;
    private final FileProps props;

    public static FileVerifiedOutput notExisted(@NonNull Path path) {
        return FileVerifiedOutput.builder().existed(false).path(path).build();
    }

    public static FileVerifiedOutput existed(@NonNull Path path, @NonNull FileProps props) {
        return FileVerifiedOutput.builder().props(props).path(path).build();
    }

    public FileVerifiedOutput validate(@NonNull FileValidator validator) {
        validator.validate(this.path, this.props);
        return this;
    }

}
