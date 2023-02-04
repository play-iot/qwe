package cloud.playio.qwe.sql.converter;

import java.util.Objects;
import java.util.UUID;

import org.jooq.Converter;

import io.github.zero88.utils.Strings;
import io.github.zero88.utils.UUID64;

public final class UUID64Converter implements Converter<UUID, String> {

    @Override
    public String from(UUID databaseObject) {
        return Objects.isNull(databaseObject) ? null : UUID64.uuidToBase64(databaseObject);
    }

    @Override
    public UUID to(String userObject) {
        return Strings.isBlank(userObject) ? null : UUID64.uuid64ToUuid(userObject);
    }

    @Override
    public Class<UUID> fromType() {
        return UUID.class;
    }

    @Override
    public Class<String> toType() {
        return String.class;
    }

}
