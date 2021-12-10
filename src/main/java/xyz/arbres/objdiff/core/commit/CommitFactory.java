package xyz.arbres.objdiff.core.commit;



import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.date.DateProvider;
import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.diff.DiffFactory;
import xyz.arbres.objdiff.core.graph.Cdo;
import xyz.arbres.objdiff.core.graph.LiveGraph;
import xyz.arbres.objdiff.core.graph.LiveGraphFactory;
import xyz.arbres.objdiff.core.graph.ObjectGraph;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.snapshot.ChangedCdoSnapshotsFactory;
import xyz.arbres.objdiff.core.snapshot.SnapshotFactory;
import xyz.arbres.objdiff.core.snapshot.SnapshotGraphFactory;
import xyz.arbres.objdiff.repository.api.ObjDiffExtendedRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static xyz.arbres.objdiff.common.validation.Validate.*;

/**
 * @author bartosz walacik
 */
public class CommitFactory {
    private final DiffFactory diffFactory;
    private final ObjDiffExtendedRepository ObjDiffRepository;
    private final DateProvider dateProvider;
    private final LiveGraphFactory liveGraphFactory;
    private final SnapshotFactory snapshotFactory;
    private final SnapshotGraphFactory snapshotGraphFactory;
    private final ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory;
    private final CommitIdFactory commitIdFactory;

    public CommitFactory(DiffFactory diffFactory, ObjDiffExtendedRepository ObjDiffRepository, DateProvider dateProvider, LiveGraphFactory liveGraphFactory, SnapshotFactory snapshotFactory, SnapshotGraphFactory snapshotGraphFactory, ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory, CommitIdFactory commitIdFactory) {
        this.diffFactory = diffFactory;
        this.ObjDiffRepository = ObjDiffRepository;
        this.dateProvider = dateProvider;
        this.liveGraphFactory = liveGraphFactory;
        this.snapshotFactory = snapshotFactory;
        this.snapshotGraphFactory = snapshotGraphFactory;
        this.changedCdoSnapshotsFactory = changedCdoSnapshotsFactory;
        this.commitIdFactory = commitIdFactory;
    }

    public Commit createTerminalByGlobalId(String author, Map<String, String> properties, GlobalId removedId){
        argumentsAreNotNull(author, properties, removedId);
        Optional<CdoSnapshot> previousSnapshot = ObjDiffRepository.getLatest(removedId);

        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        CdoSnapshot terminalSnapshot = snapshotFactory.createTerminal(removedId, previousSnapshot.orElse(null), commitMetadata);
        Diff diff = diffFactory.singleTerminal(removedId, commitMetadata);
        return new Commit(commitMetadata, Lists.asList(terminalSnapshot), diff);
    }

    public Commit createTerminal(String author, Map<String, String> properties, Object removed){
        argumentsAreNotNull(author, properties, removed);
        Cdo removedCdo = liveGraphFactory.createCdo(removed);
        return createTerminalByGlobalId(author, properties, removedCdo.getGlobalId());
    }

    public Commit create(String author, Map<String, String> properties, Object currentVersion){
        argumentsAreNotNull(author, currentVersion);
        LiveGraph currentGraph = createLiveGraph(currentVersion);
        return createCommit(author, properties, currentGraph);
    }

    private Commit createCommit(String author, Map<String, String> properties, LiveGraph currentGraph){
        CommitMetadata commitMetadata = newCommitMetadata(author, properties);
        ObjectGraph<CdoSnapshot> latestSnapshotGraph = snapshotGraphFactory.createLatest(currentGraph.globalIds());
        List<CdoSnapshot> changedCdoSnapshots =
            changedCdoSnapshotsFactory.create(currentGraph, latestSnapshotGraph.cdos(), commitMetadata);
        Diff diff = diffFactory.create(latestSnapshotGraph, currentGraph, Optional.of(commitMetadata));
        return new Commit(commitMetadata, changedCdoSnapshots, diff);
    }

    private LiveGraph createLiveGraph(Object currentVersion){
        argumentsAreNotNull(currentVersion);
        return liveGraphFactory.createLiveGraph(currentVersion);
    }

    private CommitMetadata newCommitMetadata(String author, Map<String, String> properties){
        ZonedDateTime now = dateProvider.now();
        return new CommitMetadata(author, properties,
                now.toLocalDateTime(), now.toInstant(),
                commitIdFactory.nextId());
    }
}
