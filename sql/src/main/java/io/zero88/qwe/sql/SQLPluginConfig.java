package io.zero88.qwe.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jooq.SQLDialect;

import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.provider.JooqxFacade;
import io.zero88.qwe.IConfig;
import io.zero88.qwe.IOtherConfig.HasOtherConfig;
import io.zero88.qwe.PluginConfig.DynamicPluginConfig;
import io.zero88.qwe.PluginConfig.PluginDirConfig;
import io.zero88.qwe.QWEAppConfig;
import io.zero88.qwe.utils.JsonUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public final class SQLPluginConfig extends HasOtherConfig<SQLPluginConfig>
    implements DynamicPluginConfig<SQLPluginConfig>, PluginDirConfig {

    public static final String NAME = "__sql__";

    private String pluginDir;
    private SQLDialect dialect;
    /**
     * @see JooqxFacade
     */
    private String jooqxFacadeClass;

    public SQLPluginConfig() { this(null); }

    @JsonCreator
    SQLPluginConfig(Map<String, Object> map) {
        Map<String, Object> m = Optional.ofNullable(map).orElseGet(HashMap::new);
        this.pluginDir = Optional.ofNullable(m.remove("pluginDir")).map(Object::toString).orElse(null);
        this.dialect = Optional.ofNullable(m.remove("dialect")).map(d -> SQLDialect.valueOf(d.toString())).orElse(null);
        this.jooqxFacadeClass = Strings.fallback(Objects.toString(m.remove("jooqxFacadeClass")), () -> null);
        this.putAll(m);
    }

    @Override
    public String key() {
        return NAME;
    }

    @Override
    public Class<? extends IConfig> parent() {
        return QWEAppConfig.class;
    }

    public JsonObject connectionOptions() {
        return lookup("__sql_conn__", JsonObject.class);
    }

    public JsonObject poolOptions() {
        return lookup("__sql_pool__", JsonObject.class);
    }

    @Override
    public JsonObject toJson(@NonNull ObjectMapper mapper) {
        JsonObject kv = new JsonObject().put("pluginDir", pluginDir)
                                        .put("dialect", Objects.isNull(dialect) ? null : dialect.getName())
                                        .put("jooqxFacadeClass", jooqxFacadeClass);
        return JsonUtils.putIfNotNull(super.toJson(mapper), kv.getMap());
    }

    public SQLPluginConfig validate() {
        Objects.requireNonNull(getDialect(), "Missing SQL dialect");
        return this;
    }

}
