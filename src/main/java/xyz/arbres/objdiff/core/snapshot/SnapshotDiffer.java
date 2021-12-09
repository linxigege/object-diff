package xyz.arbres.objdiff.core.snapshot;



import xyz.arbres.objdiff.common.collections.Sets;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.CoreConfiguration;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.diff.DiffFactory;
import xyz.arbres.objdiff.core.diff.changetype.ObjectRemoved;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;
import xyz.arbres.objdiff.repository.api.SnapshotIdentifier;

import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class SnapshotDiffer {

    private final DiffFactory diffFactory;
    private final CoreConfiguration ObjDiffCoreConfiguration;

    public SnapshotDiffer(DiffFactory diffFactory, CoreConfiguration ObjDiffCoreConfiguration) {
        this.diffFactory = diffFactory;
        this.ObjDiffCoreConfiguration = ObjDiffCoreConfiguration;
    }

    /**
     * Calculates changes introduced by a collection of snapshots. This method expects that
     * the previousSnapshots map contains predecessors of all non-initial and non-terminal snapshots.
     */
    public List<Change> calculateDiffs(List<CdoSnapshot> snapshots, Map<SnapshotIdentifier, CdoSnapshot> previousSnapshots) {
        Validate.argumentsAreNotNull(snapshots);
        Validate.argumentsAreNotNull(previousSnapshots);

        List<Change> changes = new ArrayList<>();
        for (CdoSnapshot snapshot : snapshots) {
            if (snapshot.isInitial()) {
                changes.addAll(addInitialChanges(snapshot));
            }
            if (snapshot.isTerminal() && !snapshot.isFirstVersion()) {
                CdoSnapshot previousSnapshot = previousSnapshots.get(SnapshotIdentifier.from(snapshot).previous());
                addTerminalChanges(changes, snapshot, previousSnapshot);
            }
            if (snapshot.isUpdate()) {
                CdoSnapshot previousSnapshot = previousSnapshots.get(SnapshotIdentifier.from(snapshot).previous());
                addChanges(changes, previousSnapshot, snapshot);
            }
        }
        return changes;
    }

    private List<Change> addInitialChanges(CdoSnapshot initialSnapshot) {
        Diff initialDiff = diffFactory.create(emptySnapshotGraph(), snapshotGraph(initialSnapshot), commitMetadata(initialSnapshot));
        return initialDiff.getChanges();
    }

    private void addTerminalChanges(List<Change> changes, CdoSnapshot terminalSnapshot, CdoSnapshot previousSnapshot) {
        changes.add(new ObjectRemoved(terminalSnapshot.getGlobalId(), empty(), of(terminalSnapshot.getCommitMetadata())));
        if (previousSnapshot != null && ObjDiffCoreConfiguration.isTerminalChanges()) {
            Diff terminalDiff = diffFactory.create(snapshotGraph(previousSnapshot), snapshotGraph(terminalSnapshot), commitMetadata(terminalSnapshot));
            changes.addAll(terminalDiff.getChanges());
        }
    }

    private void addChanges(List<Change> changes, CdoSnapshot previousSnapshot, CdoSnapshot currentSnapshot) {
        Diff diff = diffFactory.create(snapshotGraph(previousSnapshot), snapshotGraph(currentSnapshot), commitMetadata(currentSnapshot));
        changes.addAll(diff.getChanges());
    }

    private SnapshotGraph snapshotGraph(CdoSnapshot snapshot) {
        return new SnapshotGraph(Sets.asSet(new SnapshotNode(snapshot)));
    }

    private SnapshotGraph emptySnapshotGraph() {
        return new SnapshotGraph(Collections.emptySet());
    }

    private Optional<CommitMetadata> commitMetadata(CdoSnapshot snapshot) {
        return of(snapshot.getCommitMetadata());
    }
}
