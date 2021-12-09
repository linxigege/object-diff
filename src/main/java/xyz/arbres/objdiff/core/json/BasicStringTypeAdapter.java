package xyz.arbres.objdiff.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * Convenient abstract implementation of {@link JsonTypeAdapter}.
 * Extend it if you need to represent your {@link ValueType} as a single String.
 * <p/>
 *
 * For a concrete adapter implementation example see {@link org.ObjDiff.java8support.LocalDateTimeTypeAdapter}.
 *
 * @author bartosz walacik
 */
public abstract class BasicStringTypeAdapter<T> extends JsonTypeAdapterTemplate<T> {

    /**
     * Example serialization for LocalDateTime:
     * <pre>
     * public String serialize(LocalDateTime sourceValue) {
     *     return ISO_DATE_TIME_FORMATTER.print(sourceValue);
     * }
     * </pre>
     * @param sourceValue not null
     */
    public abstract String serialize(T sourceValue);

    /**
     * Example deserialization for LocalDateTime:
     * <pre>
     * public LocalDateTime deserialize(String serializedValue) {
     *     return ISO_DATE_TIME_FORMATTER.parseLocalDateTime(serializedValue);
     * }
     * </pre>
     *
     * @param serializedValue not null
     */
    public abstract T deserialize(String serializedValue);

    @Override
    public T fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext ) {
        return deserialize(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement toJson(T sourceValue, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(serialize(sourceValue));
    }
}
