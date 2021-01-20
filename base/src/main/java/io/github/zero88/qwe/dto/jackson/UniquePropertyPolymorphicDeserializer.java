package io.github.zero88.qwe.dto.jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import io.github.zero88.qwe.exceptions.DesiredException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class UniquePropertyPolymorphicDeserializer<T> extends StdDeserializer<T> {

    private final Map<String, Class<? extends T>> registry;

    public UniquePropertyPolymorphicDeserializer(Class<T> clazz) {
        super(clazz);
        registry = new HashMap<>();
    }

    public void register(String uniqueProperty, Class<? extends T> clazz) {
        registry.put(uniqueProperty, clazz);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode tree = mapper.readTree(jp);
        try {
            return mapper.treeToValue(tree, findDeserializationClass(tree));
        } catch (NullPointerException e) {
            throw JsonMappingException.from(jp, e.getMessage());
        } catch (DesiredException e) {
            return null;
        }
    }

    private Class<? extends T> findDeserializationClass(JsonNode tree) {
        Iterator<String> fields = tree.fieldNames();
        while (fields.hasNext()) {
            String property = fields.next();
            return Objects.requireNonNull(registry.get(property), "Unrecognized property key " + property);
        }
        throw new DesiredException("blank");
    }

}
