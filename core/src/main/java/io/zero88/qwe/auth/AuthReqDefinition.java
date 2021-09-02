package io.zero88.qwe.auth;

import java.util.List;

import io.zero88.qwe.dto.JsonData;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public final class AuthReqDefinition implements JsonData {

    private boolean loginRequired = true;
    private List<String> allowRoles;
    private List<String> allowPerms;
    private List<String> allowGroups;
    private String customAccessRule;

}
