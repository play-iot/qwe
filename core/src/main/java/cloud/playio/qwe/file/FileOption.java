package cloud.playio.qwe.file;

import cloud.playio.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@Builder(builderClassName = "Builder")
public class FileOption implements JsonData {

    @Default
    private final boolean autoCreate = true;
    @Default
    private final boolean overwrite = true;
    @Default
    private final boolean strict = true;
    @Default
    private final String owner = System.getProperty("user.name");
    @Default
    private final String filePerms = "rw-r--r--";
    @Default
    private final String folderPerms = "rwxr-xr-x";

    public static FileOption create() {
        return FileOption.builder().build();
    }

}
