package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.string.ShaDigest;
import xyz.arbres.objdiff.core.json.JsonConverter;
import xyz.arbres.objdiff.core.snapshot.SnapshotFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bartosz.walacik
 */
class ObjectHasher {
    private final SnapshotFactory snapshotFactory;
    private final JsonConverter jsonConverter;

    ObjectHasher(SnapshotFactory snapshotFactory, JsonConverter jsonConverter) {
        this.snapshotFactory = snapshotFactory;
        this.jsonConverter = jsonConverter;
    }

    String hash(List<LiveCdo> objects) {
        String jsonState = objects.stream().map(cdo -> snapshotFactory.createSnapshotStateNoRefs(cdo))
                .map(state -> jsonConverter.toJson(state))
                .sorted()
                .collect(Collectors.joining("\n"));
        return ShaDigest.longDigest(jsonState);
    }
}
