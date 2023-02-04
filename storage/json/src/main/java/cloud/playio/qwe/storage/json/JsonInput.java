package cloud.playio.qwe.storage.json;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import io.github.zero88.utils.DateTimes;
import io.github.zero88.utils.Strings;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import cloud.playio.qwe.dto.JsonData;
import cloud.playio.qwe.file.FileOption;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public final class JsonInput implements JsonData {

    @Default
    private final boolean fileAsJson = true;
    @Default
    private final String outputKey = "data";
    @NonNull
    private final String file;
    private final String pointer;
    private final Object dataToInsert;
    private final Object keyToRemove;
    @Default
    private final boolean skipRemovedKeyInOutput = false;
    private final FileOption fileOption;

    public @NonNull JsonPointer pointer() {
        return Strings.isBlank(pointer) ? JsonPointer.create() : JsonPointer.from(pointer);
    }

    public static class JsonInputBuilder {

        @JsonProperty("dataToInsert")
        private JsonInputBuilder toInsert(Object dataToInsert) {
            this.dataToInsert = dataToInsert;
            return this;
        }

        @JsonProperty("keyToRemove")
        private JsonInputBuilder toRemove(Object keyToRemove) {
            this.keyToRemove = keyToRemove;
            return this;
        }

        public JsonInputBuilder keyToRemove(@NonNull String keyToRemove) {
            return this.toRemove(keyToRemove);
        }

        public JsonInputBuilder keyToRemove(int keyToRemove) {
            return this.toRemove(keyToRemove);
        }

        public JsonInputBuilder dataToInsert(JsonData dataToInsert) {
            return this.toInsert(Optional.ofNullable(dataToInsert).map(JsonData::toJson).orElse(null));
        }

        public JsonInputBuilder dataToInsert(JsonObject dataToInsert) {
            return this.toInsert(dataToInsert);
        }

        public JsonInputBuilder dataToInsert(JsonArray dataToInsert) {
            return this.toInsert(dataToInsert);
        }

        public JsonInputBuilder dataToInsert(String dataToInsert) {
            return this.toInsert(dataToInsert);
        }

        public JsonInputBuilder dataToInsert(Number dataToInsert) {
            return this.toInsert(dataToInsert);
        }

        public JsonInputBuilder dataToInsert(Boolean dataToInsert) {
            return this.toInsert(dataToInsert);
        }

        public JsonInputBuilder dataToInsert(Date dataToInsert) {
            return this.toInsert(Optional.ofNullable(dataToInsert)
                                         .map(d -> DateTimes.Iso8601Formatter.format(DateTimes.toUTC(d)))
                                         .orElse(null));
        }

        public JsonInputBuilder dataToInsert(Instant dataToInsert) {
            return this.toInsert(DateTimes.Iso8601Formatter.format(DateTimes.from(dataToInsert)));
        }

    }

}
