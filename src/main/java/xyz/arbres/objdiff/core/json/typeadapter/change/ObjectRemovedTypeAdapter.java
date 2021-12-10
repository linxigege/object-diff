package xyz.arbres.objdiff.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.changetype.ObjectRemoved;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;

import java.util.Optional;

import static java.util.Optional.ofNullable;

class ObjectRemovedTypeAdapter extends ChangeTypeAdapter<ObjectRemoved> {

    public ObjectRemovedTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ObjectRemoved fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ObjectRemoved(deserializeAffectedCdoId(jsonObject,context),Optional.empty(), ofNullable(commitMetadata));
    }

    @Override
    public Class getValueType() {
        return ObjectRemoved.class;
    }
}
