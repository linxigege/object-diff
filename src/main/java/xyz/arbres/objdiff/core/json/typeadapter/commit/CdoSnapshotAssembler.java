package xyz.arbres.objdiff.core.json.typeadapter.commit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.arbres.objdiff.core.json.CdoSnapshotSerialized;
import xyz.arbres.objdiff.core.json.JsonConverter;

public class CdoSnapshotAssembler {
    private final JsonConverter jsonConverter;

    public CdoSnapshotAssembler(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public JsonElement assemble(CdoSnapshotSerialized snapshot) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add(CdoSnapshotTypeAdapter.COMMIT_METADATA, assembleCommitMetadata(snapshot));
        jsonObject.add(CdoSnapshotTypeAdapter.STATE_NAME, jsonConverter.fromJsonToJsonElement(snapshot.getSnapshotState()));
        jsonObject.add(CdoSnapshotTypeAdapter.CHANGED_NAME, assembleChangedPropNames(snapshot));
        jsonObject.addProperty(CdoSnapshotTypeAdapter.TYPE_NAME, snapshot.getSnapshotType());
        jsonObject.addProperty(CdoSnapshotTypeAdapter.VERSION, snapshot.getVersion());
        jsonObject.add(CdoSnapshotTypeAdapter.GLOBAL_CDO_ID, assembleGlobalId(snapshot));

        return jsonObject;
    }

    private JsonElement assembleGlobalId(CdoSnapshotSerialized snapshot) {
        String fragment = snapshot.getGlobalIdFragment();
        String localIdJSON = snapshot.getGlobalIdLocalId();
        String cdoType = snapshot.getGlobalIdTypeName();
        String ownerFragment = snapshot.getOwnerGlobalIdFragment();
        String ownerLocalId = snapshot.getOwnerGlobalIdLocalId();
        String ownerCdoType = snapshot.getOwnerGlobalIdTypeName();

        JsonObject json = assembleOneGlobalId(cdoType, localIdJSON, fragment);
        if (ownerFragment != null || ownerLocalId != null || ownerCdoType != null) {
            JsonObject ownerId = assembleOneGlobalId(ownerCdoType, ownerLocalId, ownerFragment);
            json.add(GlobalIdTypeAdapter.OWNER_ID_FIELD, ownerId);
        }
        return json;
    }

    private JsonObject assembleOneGlobalId(String typeName, String localIdJson, String fragment) {
        JsonObject json = new JsonObject();
        if (localIdJson != null) {
            json.addProperty(GlobalIdTypeAdapter.ENTITY_FIELD, typeName);
            json.add(GlobalIdTypeAdapter.CDO_ID_FIELD, jsonConverter.fromJsonToJsonElement(localIdJson));
        } else {
            json.addProperty(GlobalIdTypeAdapter.VALUE_OBJECT_FIELD, typeName);
            json.addProperty(GlobalIdTypeAdapter.FRAGMENT_FIELD, fragment);
        }
        return json;
    }

    private JsonElement assembleChangedPropNames(CdoSnapshotSerialized snapshot) {
        String changed = snapshot.getChangedProperties();
        if (changed == null || changed.isEmpty()) {
            return new JsonObject();
        }
        return jsonConverter.fromJsonToJsonElement(changed);
    }

    private JsonElement assembleCommitMetadata(CdoSnapshotSerialized snapshot) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CommitMetadataTypeAdapter.AUTHOR, snapshot.getCommitAuthor());
        jsonObject.add(CommitMetadataTypeAdapter.PROPERTIES, CommitPropertiesConverter.toJson(snapshot.getCommitProperties()));
        jsonObject.add(CommitMetadataTypeAdapter.COMMIT_DATE, jsonConverter.toJsonElement(snapshot.getCommitDate()));
        jsonObject.add(CommitMetadataTypeAdapter.COMMIT_DATE_INSTANT, jsonConverter.toJsonElement(snapshot.getCommitDateInstant()));
        jsonObject.add(CommitMetadataTypeAdapter.COMMIT_ID, jsonConverter.toJsonElement(snapshot.getCommitId()));

        return jsonObject;
    }
}
