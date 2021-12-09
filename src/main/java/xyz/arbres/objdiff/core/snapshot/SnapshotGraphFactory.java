package xyz.arbres.objdiff.core.snapshot;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds SnapshotGraph from latest snapshots loaded from ObjDiffRepository
 */
public class SnapshotGraphFactory {
    private final ObjDiffExtendedRepository ObjDiffRepository;

    SnapshotGraphFactory(ObjDiffExtendedRepository ObjDiffRepository) {
        this.ObjDiffRepository = ObjDiffRepository;
    }

    public SnapshotGraph createLatest(Set<GlobalId> globalIds){
        Validate.argumentIsNotNull(globalIds);

        Set<SnapshotNode> snapshotNodes = ObjDiffRepository.getLatest(globalIds)
                .stream()
                .map(SnapshotNode::new)
                .collect(Collectors.toSet());

        return new SnapshotGraph(snapshotNodes);
    }
}
