package io.zero88.qwe;

import io.github.zero88.utils.Strings;
import io.zero88.qwe.dto.JsonData;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public final class ApplicationVersion implements JsonData {

    private final String name;
    private final String title;
    private final String version;
    private final String hashVersion;
    private final String coreVersion;

    @Override
    public String toString() {
        String ver = version + (Strings.isBlank(hashVersion) ? "" : "-" + hashVersion);
        // @formatter:off
        return new StringBuilder().append("App Name    : ").append(name).append("\n")
                                  .append("App Title   : ").append(title).append("\n")
                                  .append("App Version : ").append(ver).append("\n")
                                  .append("Core Version: ").append(coreVersion)
                                  .toString();
        // @formatter:on
    }

}
