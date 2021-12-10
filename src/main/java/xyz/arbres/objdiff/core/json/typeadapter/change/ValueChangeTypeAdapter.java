package xyz.arbres.objdiff.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.changetype.InitialValueChange;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChangeMetadata;
import xyz.arbres.objdiff.core.diff.changetype.TerminalValueChange;
import xyz.arbres.objdiff.core.diff.changetype.ValueChange;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;

import java.util.List;

class ValueChangeTypeAdapter extends ChangeTypeAdapter<ValueChange> {
    private static final String LEFT_VALUE_FIELD = "left";
    private static final String RIGHT_VALUE_FIELD = "right";

    public ValueChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ValueChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeMetadata stub = deserializeStub(jsonObject, context);

        Object leftValue  = context.deserialize(jsonObject.get(LEFT_VALUE_FIELD),  getObjDiffProperty(stub).getGenericType());
        Object rightValue = context.deserialize(jsonObject.get(RIGHT_VALUE_FIELD), getObjDiffProperty(stub).getGenericType());

        Class<? extends Change> changeType = decodeChangeType((JsonObject) json);

        if (changeType == ValueChange.class) {
            return new ValueChange(stub, leftValue, rightValue);
        }
        if (changeType == InitialValueChange.class) {
            return new InitialValueChange(stub, rightValue);
        }
        if (changeType == TerminalValueChange.class) {
            return new TerminalValueChange(stub, leftValue);
        }
        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public JsonElement toJson(ValueChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_VALUE_FIELD, context.serialize(change.getLeft()));
        jsonObject.add(RIGHT_VALUE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public List<Class> getValueTypes() {
        return Lists.asList(ValueChange.class, InitialValueChange.class, TerminalValueChange.class);
    }
}
