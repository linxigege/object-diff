package xyz.arbres.objdiff.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.changetype.*;
import xyz.arbres.objdiff.core.diff.changetype.container.ArrayChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.diff.changetype.container.SetChange;
import xyz.arbres.objdiff.core.diff.changetype.map.MapChange;
import xyz.arbres.objdiff.core.json.JsonTypeAdapterTemplate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ChangeTypeAdapter<T extends Change> extends JsonTypeAdapterTemplate<T> {

    private static final String CHANGE_TYPE_FIELD = "changeType";
    private static final String AFFECTED_CDO_ID_FIELD = "globalId";
    private static final String PROPERTY_FIELD = "property";
    private static final String COMMIT_METADATA = "commitMetadata";
    private static final String PROPERTY_CHANGE_TYPE = "propertyChangeType";

    private final Map<String, Class<? extends Change>> changeTypeMap;
    protected final TypeMapper typeMapper;

    public ChangeTypeAdapter(TypeMapper typeMapper) {
        this.changeTypeMap = new HashMap<>();
        this.typeMapper = typeMapper;
        initEntry(ValueChange.class);
        initEntry(InitialValueChange.class);
        initEntry(TerminalValueChange.class);
        initEntry(ReferenceChange.class);
        initEntry(NewObject.class);
        initEntry(ObjectRemoved.class);
        initEntry(MapChange.class);
        initEntry(ListChange.class);
        initEntry(ArrayChange.class);
        initEntry(SetChange.class);
    }

    protected CommitMetadata deserializeCommitMetadata(JsonObject jsonObject, JsonDeserializationContext context) {
        return context.deserialize(jsonObject.get(COMMIT_METADATA), CommitMetadata.class);
    }

    protected ManagedType getManagedType(PropertyChangeMetadata stub) {
        return typeMapper.getObjDiffManagedType(stub.getAffectedCdoId());
    }

    protected ObjDiffProperty getObjDiffProperty(PropertyChangeMetadata stub) {
        return getManagedType(stub).getProperty(stub.getPropertyName());
    }

    @Override
    public T fromJson(JsonElement json, JsonDeserializationContext context) {
        Class<? extends Change> changeType = decodeChangeType((JsonObject) json);
        return context.deserialize(json, changeType);
    }

    @Override
    public JsonElement toJson(T change, JsonSerializationContext context) {
        return createJsonObject(change, context);
    }

    protected PropertyChangeMetadata deserializeStub(JsonObject jsonObject, JsonDeserializationContext context) {
        GlobalId id = deserializeAffectedCdoId(jsonObject, context);
        String propertyName = jsonObject.get(PROPERTY_FIELD).getAsString();
        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);

        PropertyChangeType propertyChangeType = jsonObject.get(PROPERTY_CHANGE_TYPE) != null
            ? PropertyChangeType.valueOf(jsonObject.get(PROPERTY_CHANGE_TYPE).getAsString())
            : PropertyChangeType.PROPERTY_VALUE_CHANGED;

        return new PropertyChangeMetadata(id, propertyName, Optional.ofNullable(commitMetadata), propertyChangeType);
    }

    protected GlobalId deserializeAffectedCdoId(JsonObject jsonObject, JsonDeserializationContext context) {
        return context.deserialize(jsonObject.get(AFFECTED_CDO_ID_FIELD), GlobalId.class);
    }

    protected JsonObject createJsonObject(T change, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CHANGE_TYPE_FIELD, encode(change.getClass()));
        jsonObject.add(AFFECTED_CDO_ID_FIELD, context.serialize(change.getAffectedGlobalId()));

        if (change.getCommitMetadata().isPresent()) {
            jsonObject.add(COMMIT_METADATA, context.serialize(change.getCommitMetadata().get()));
        }

        if (change instanceof PropertyChange) {
            jsonObject.addProperty(PROPERTY_FIELD, ((PropertyChange) change).getPropertyName());
            PropertyChangeType changeType =  ((PropertyChange) change).getChangeType();
            if (changeType != null) {
                jsonObject.addProperty(PROPERTY_CHANGE_TYPE, changeType.name());
            }
        }
        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return Change.class;
    }

    private void initEntry(Class<? extends Change> changeClass) {
        changeTypeMap.put(encode(changeClass), changeClass);
    }

    private String encode(Class<? extends Change> valueChangeClass) {
        return valueChangeClass.getSimpleName();
    }

    Class<? extends Change> decodeChangeType(JsonObject jsonObject){
        String changeTypeField = jsonObject.get(CHANGE_TYPE_FIELD).getAsString();
        if (!changeTypeMap.containsKey(changeTypeField)) {
            throw new ObjDiffException(ObjDiffExceptionCode.MALFORMED_CHANGE_TYPE_FIELD, changeTypeField);
        }
        return changeTypeMap.get(changeTypeField);
    }
}
