package xyz.arbres.objdiff.core.json.typeadapter.change;

import com.google.gson.*;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChangeMetadata;
import xyz.arbres.objdiff.core.diff.changetype.map.*;
import xyz.arbres.objdiff.core.metamodel.type.MapType;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class MapChangeTypeAdapter extends ChangeTypeAdapter<MapChange> {

    private static final String ENTRY_CHANGES_FIELD = "entryChanges";
    private static final String ENTRY_CHANGE_TYPE_FIELD = "entryChangeType";
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    private static final String LEFT_VALUE_FIELD = "leftValue";
    private static final String RIGHT_VALUE_FIELD = "rightValue";

    public MapChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public MapChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeMetadata stub = deserializeStub(jsonObject, context);

        MapType mapType = getObjDiffProperty(stub).getType();
        List<EntryChange> changes = parseChanges(jsonObject, context, mapType);

        return new MapChange(stub, changes);
    }

    @Override
    public JsonElement toJson(MapChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        appendBody(change, jsonObject, context);

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return MapChange.class;
    }

    private List<EntryChange> parseChanges(JsonObject jsonObject, JsonDeserializationContext context, MapType mapType) {
        List<EntryChange> result = new ArrayList<>();

        JsonArray array = jsonObject.getAsJsonArray(ENTRY_CHANGES_FIELD);

        for (JsonElement e : array) {
            JsonObject entryChange = (JsonObject) e;
            String entryChangeType = entryChange.get(ENTRY_CHANGE_TYPE_FIELD).getAsString();

            if (EntryAdded.class.getSimpleName().equals(entryChangeType)) {
                result.add(parseEntryAdded(entryChange, context, mapType));
            } else if (EntryRemoved.class.getSimpleName().equals(entryChangeType)) {
                result.add(parseEntryRemoved(entryChange, context, mapType));
            } else if (EntryValueChange.class.getSimpleName().equals(entryChangeType)) {
                result.add(parseEntryValueChange(entryChange, context, mapType));
            } else {
                throw new ObjDiffException(ObjDiffExceptionCode.MALFORMED_ENTRY_CHANGE_TYPE_FIELD, entryChangeType);
            }
        }

        return result;
    }

    private EntryAdded parseEntryAdded(JsonObject entryChange, JsonDeserializationContext context, MapType mapType) {
        Object key = decodeValue(entryChange, context, KEY_FIELD, mapType.getKeyJavaType());
        Object value = decodeValue(entryChange, context, VALUE_FIELD, mapType.getValueJavaType());
        return new EntryAdded(key, value);
    }

    private EntryRemoved parseEntryRemoved(JsonObject entryChange, JsonDeserializationContext context, MapType mapType) {
        Object key = decodeValue(entryChange, context, KEY_FIELD, mapType.getKeyJavaType());
        Object value = decodeValue(entryChange, context, VALUE_FIELD, mapType.getValueJavaType());
        return new EntryRemoved(key, value);
    }

    private EntryValueChange parseEntryValueChange(JsonObject entryChange, JsonDeserializationContext context, MapType mapType) {
        Object key = decodeValue(entryChange, context, KEY_FIELD, mapType.getKeyJavaType());
        Object leftValue = decodeValue(entryChange, context, LEFT_VALUE_FIELD, mapType.getValueJavaType());
        Object rightValue = decodeValue(entryChange, context, RIGHT_VALUE_FIELD, mapType.getValueJavaType());
        return new EntryValueChange(key, leftValue, rightValue);
    }

    private Object decodeValue(JsonObject entryChange, JsonDeserializationContext context, String fieldName, Type expectedType) {
        return context.deserialize(entryChange.get(fieldName), typeMapper.getDehydratedType(expectedType));
    }

    private void appendBody(MapChange change, JsonObject toJson, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        for (EntryChange entryChange : (List<EntryChange>) change.getEntryChanges()) {
            JsonObject jsonElement = new JsonObject();
            jsonElement.addProperty(ENTRY_CHANGE_TYPE_FIELD, entryChange.getClass().getSimpleName());

            if (entryChange instanceof EntryAddOrRemove) {
                EntryAddOrRemove entry = (EntryAddOrRemove) entryChange;

                jsonElement.add(KEY_FIELD, context.serialize(entry.getWrappedKey()));
                jsonElement.add(VALUE_FIELD, context.serialize(entry.getWrappedValue()));
            }

            if (entryChange instanceof EntryValueChange) {
                EntryValueChange entry = (EntryValueChange) entryChange;
                jsonElement.add(KEY_FIELD, context.serialize(entry.getWrappedKey()));
                jsonElement.add(LEFT_VALUE_FIELD, context.serialize(entry.getWrappedLeftValue()));
                jsonElement.add(RIGHT_VALUE_FIELD, context.serialize(entry.getWrappedRightValue()));
            }
            jsonArray.add(jsonElement);
        }
        toJson.add(ENTRY_CHANGES_FIELD, jsonArray);
    }
}
