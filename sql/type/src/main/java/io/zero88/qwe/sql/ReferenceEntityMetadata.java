package cloud.playio.qwe.sql;

import java.util.Objects;

import org.jooq.ForeignKey;
import org.jooq.Table;
import org.jooq.TableField;

import cloud.playio.qwe.exceptions.ErrorCode;
import cloud.playio.qwe.exceptions.ImplementationError;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder(builderClassName = "Builder")
public final class ReferenceEntityMetadata {

    @NonNull
    private final ForeignKey foreignKey;
    private final Table table;

    public boolean isValid() {
        return this.foreignKey.getFields().size() == 1 && Objects.nonNull(table);
    }

    public TableField getField() {
        return (TableField) this.foreignKey.getFields().get(0);
    }

    public @NonNull EntityMetadata findByTable(@NonNull MetadataIndex index) {
        return index.findByTable(table)
                    .orElseThrow(() -> new ImplementationError(ErrorCode.parse("SERVICE_NOT_FOUND"),
                                                               "Not found Entity Metadata by table " +
                                                               table.getName()));
    }

    public static final class Builder {

        public ReferenceEntityMetadata build() {
            return new ReferenceEntityMetadata(foreignKey, Objects.isNull(table) ? foreignKey.getTable() : table);
        }

    }

}
