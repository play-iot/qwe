package io.zero88.qwe.http.server.upload;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.utils.FileUtils;
import io.vertx.ext.web.FileUpload;
import io.zero88.qwe.Wrapper;
import io.zero88.qwe.dto.JsonData;
import io.zero88.qwe.http.server.HttpServerConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
public final class FileUploadWrapper implements Wrapper<FileUpload>, JsonData {

    @JsonIgnore
    private final FileUpload fileUpload;
    private final String name;
    private final String fileName;
    private final String filePath;
    private final String extension;
    private final String charset;
    private final String contentType;
    private final String transferEncoding;
    private final long size;

    public static FileUploadWrapper create(Path uploadDir, FileUpload fileUpload) {
        return FileUploadWrapper.builder()
                                .fileUpload(Objects.requireNonNull(fileUpload, "File upload is null"))
                                .name(fileUpload.name())
                                .fileName(fileUpload.fileName())
                                .filePath(uploadDir.relativize(Paths.get(fileUpload.uploadedFileName())).toString())
                                .extension(FileUtils.getExtension(fileUpload.fileName()))
                                .charset(fileUpload.charSet())
                                .contentType(fileUpload.contentType())
                                .transferEncoding(fileUpload.contentTransferEncoding())
                                .size(fileUpload.size() * HttpServerConfig.MB)
                                .build();
    }

    @Override
    public @Nullable FileUpload unwrap() {
        return fileUpload;
    }

}
