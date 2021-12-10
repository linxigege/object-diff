package xyz.arbres.objdiff.core.json.typeadapter.commit;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.diff.DiffBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class DiffTypeDeserializer implements JsonDeserializer<Diff> {
    private static final String CHANGES_FIELD = "changes";

    @Override
    public Diff deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement changesObject = ((JsonObject)json).get(CHANGES_FIELD);

        if (changesObject != null) {
            List<Change> changes = context.deserialize(changesObject, new TypeToken<List<Change>>(){}.getType());
            return new DiffBuilder()
                    .addChanges(changes)
                    .build();
        }
        return DiffBuilder.empty();
    }
}
