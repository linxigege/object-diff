package xyz.arbres.objdiff.core.json;

import com.google.gson.*;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.json.typeadapter.util.UtilTypeCoreAdapters;
import xyz.arbres.objdiff.core.metamodel.annotation.DiffIgnore;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author bartosz walacik
 * @see JsonConverter
 */
public class JsonConverterBuilder {
    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final GsonBuilder gsonBuilder;
    private boolean typeSafeValues = false;
    private boolean prettyPrint;

    public JsonConverterBuilder() {
        this.gsonBuilder = new GsonBuilder();
        this.gsonBuilder.setExclusionStrategies(new SkipFieldExclusionStrategy());
        registerBuiltInAdapters((List) UtilTypeCoreAdapters.adapters());
    }

    /**
     * When switched to true, all {@link org.ObjDiff.core.diff.changetype.Atomic}s
     * are serialized type safely as a type + value pair, for example:
     * <pre>
     * {
     *     "typeAlias": "LocalDate"
     *     "value": "2001-01-01"
     * }
     * </pre>
     * TypeAlias is defaulted to value.class.simpleName.
     * <br/><br/>
     * <p>
     * Useful when serializing polymorfic collections like List or List&lt;Object&gt;
     *
     * @param typeSafeValues default false
     */
    public JsonConverterBuilder typeSafeValues(boolean typeSafeValues) {
        this.typeSafeValues = typeSafeValues;
        return this;
    }

    /**
     * @param prettyPrint default true
     */
    public JsonConverterBuilder prettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }

    /**
     * @param nativeAdapter should be null safe, if not so,
     *                      simply call {@link TypeAdapter#nullSafe()} before registering it
     * @see TypeAdapter
     */
    public JsonConverterBuilder registerNativeTypeAdapter(Type targetType, TypeAdapter nativeAdapter) {
        Validate.argumentsAreNotNull(targetType, nativeAdapter);
        gsonBuilder.registerTypeAdapter(targetType, nativeAdapter);
        return this;
    }

    /**
     * @see JsonSerializer
     */
    public JsonConverterBuilder registerNativeGsonSerializer(Type targetType, JsonSerializer<?> jsonSerializer) {
        Validate.argumentsAreNotNull(targetType, jsonSerializer);
        gsonBuilder.registerTypeAdapter(targetType, jsonSerializer);
        return this;
    }

    /**
     * @see JsonSerializer
     * @since 3.1
     */
    public JsonConverterBuilder registerNativeGsonHierarchySerializer(Class targetType, JsonSerializer<?> jsonSerializer) {
        Validate.argumentsAreNotNull(targetType, jsonSerializer);
        gsonBuilder.registerTypeHierarchyAdapter(targetType, jsonSerializer);
        return this;
    }

    /**
     * @see JsonDeserializer
     * @since 3.1
     */
    public JsonConverterBuilder registerNativeGsonHierarchyDeserializer(Class targetType, JsonDeserializer<?> jsonDeserializer) {
        Validate.argumentsAreNotNull(targetType, jsonDeserializer);
        gsonBuilder.registerTypeHierarchyAdapter(targetType, jsonDeserializer);
        return this;
    }

    /**
     * @see JsonDeserializer
     */
    public JsonConverterBuilder registerNativeGsonDeserializer(Type targetType, JsonDeserializer<?> jsonDeserializer) {
        Validate.argumentsAreNotNull(targetType, jsonDeserializer);
        gsonBuilder.registerTypeAdapter(targetType, jsonDeserializer);
        return this;
    }

    public JsonConverterBuilder registerJsonTypeAdapters(Collection<JsonTypeAdapter> adapters) {
        Validate.argumentIsNotNull(adapters);
        for (JsonTypeAdapter adapter : adapters) {
            registerJsonTypeAdapter(adapter);
        }
        return this;
    }

    /**
     * Maps given {@link JsonTypeAdapter}
     * into pair of {@link JsonDeserializer} and {@link JsonDeserializer}
     * and registers them with this.gsonBuilder
     */
    public JsonConverterBuilder registerJsonTypeAdapter(JsonTypeAdapter adapter) {
        Validate.argumentIsNotNull(adapter);
        adapter.getValueTypes().forEach(c -> registerJsonTypeAdapterForType((Class) c, adapter));
        return this;
    }


    public JsonConverter build() {
        registerBuiltInAdapter(new AtomicTypeAdapter(typeSafeValues));

        if (prettyPrint) {
            gsonBuilder.setPrettyPrinting();
        }

        gsonBuilder.enableComplexMapKeySerialization();

        gsonBuilder.serializeNulls()
                .setDateFormat(ISO_DATE_TIME_FORMAT);

        return new JsonConverter(gsonBuilder.create());
    }

    private void registerJsonTypeAdapterForType(Class targetType, final JsonTypeAdapter adapter) {
        JsonSerializer jsonSerializer = (value, type, jsonSerializationContext) -> adapter.toJson(value, jsonSerializationContext);
        JsonDeserializer jsonDeserializer = (jsonElement, type, jsonDeserializationContext) -> adapter.fromJson(jsonElement, jsonDeserializationContext);

        registerNativeGsonSerializer(targetType, jsonSerializer);
        registerNativeGsonDeserializer(targetType, jsonDeserializer);
    }

    private void registerBuiltInAdapters(final List<JsonTypeAdapter> adapters) {
        adapters.forEach(this::registerBuiltInAdapter);
    }

    private void registerBuiltInAdapter(final JsonTypeAdapter adapter) {
        adapter.getValueTypes().forEach(c -> registerJsonTypeAdapterForType((Class) c, adapter));
    }

    private static class SkipFieldExclusionStrategy implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> clazz) {
            return clazz.getAnnotation(DiffIgnore.class) != null;
        }

        public boolean shouldSkipField(FieldAttributes field) {
            return field.getAnnotation(DiffIgnore.class) != null;
        }
    }
}
