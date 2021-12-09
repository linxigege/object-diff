package xyz.arbres.objdiff.core.snapshot;


import xyz.arbres.objdiff.core.graph.ObjectGraph;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;

import java.util.Set;

/**
 * @author bartosz walacik
 */
class SnapshotGraph extends ObjectGraph<CdoSnapshot> {
    SnapshotGraph(Set<SnapshotNode> snapshots) {
        super((Set)snapshots);
    }
}
