package xyz.arbres.objdiff.repository.api;

import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.commit.Commit;
import xyz.arbres.objdiff.core.commit.CommitId;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChange;
import xyz.arbres.objdiff.core.json.JsonConverter;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.object.InstanceId;
import xyz.arbres.objdiff.core.metamodel.type.EntityType;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;
import xyz.arbres.objdiff.core.snapshot.SnapshotDiffer;

import java.time.LocalDateTime;
import java.util.*;

import static xyz.arbres.objdiff.common.validation.Validate.*;

public class ObjDiffExtendedRepository implements ObjDiffRepository {
    private final ObjDiffRepository delegate;
    private final SnapshotDiffer snapshotDiffer;
    private final PreviousSnapshotsCalculator previousSnapshotsCalculator;

    public ObjDiffExtendedRepository(ObjDiffRepository delegate, SnapshotDiffer snapshotDiffer) {
        this.delegate = delegate;
        this.snapshotDiffer = snapshotDiffer;
        previousSnapshotsCalculator = new PreviousSnapshotsCalculator(input -> getSnapshots(input));
    }

    public List<Change> getChangeHistory(GlobalId globalId, QueryParams queryParams) {
        argumentsAreNotNull(globalId, queryParams);

        List<CdoSnapshot> snapshots = getStateHistory(globalId, queryParams);
        List<Change> changes = getChangesIntroducedBySnapshots(snapshots);

        return filterChangesByPropertyNames(changes, queryParams);
    }

    public List<Change> getChangeHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {


        List<CdoSnapshot> snapshots = getStateHistory(givenClasses, queryParams);
        List<Change> changes = getChangesIntroducedBySnapshots(snapshots);
        return filterChangesByPropertyNames(changes, queryParams);
    }

    public List<Change> getValueObjectChangeHistory(EntityType ownerEntity, String path, QueryParams queryParams) {


        List<CdoSnapshot> snapshots = getValueObjectStateHistory(ownerEntity, path, queryParams);
        return getChangesIntroducedBySnapshots(snapshots);
    }

    public List<Change> getChanges(QueryParams queryParams) {


        List<CdoSnapshot> snapshots = getSnapshots(queryParams);
        return getChangesIntroducedBySnapshots(snapshots);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {


        List<CdoSnapshot> snapshots = delegate.getStateHistory(globalId, queryParams);

        if (globalId instanceof InstanceId && queryParams.isAggregate()) {
            return loadMasterEntitySnapshotIfNecessary((InstanceId) globalId, snapshots);
        } else {
            return snapshots;
        }
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {


        return delegate.getValueObjectStateHistory(ownerEntity, path, queryParams);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {


        return delegate.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds) {


        return delegate.getLatest(globalIds);
    }

    /**
     * last snapshot with commitId <= given timePoint
     */
    public List<CdoSnapshot> getHistoricals(GlobalId globalId, CommitId timePoint, boolean withChildValueObjects, int limit) {
        argumentsAreNotNull(globalId, timePoint);

        return delegate.getStateHistory(globalId, QueryParamsBuilder
                .withLimit(limit)
                .withChildValueObjects(withChildValueObjects)
                .toCommitId(timePoint).build());
    }

    /**
     * last snapshot with commitId <= given date
     */
    public Optional<CdoSnapshot> getHistorical(GlobalId globalId, LocalDateTime timePoint) {
        argumentsAreNotNull(globalId, timePoint);

        return delegate.getStateHistory(globalId, QueryParamsBuilder.withLimit(1).to(timePoint).build())
                .stream().findFirst();
    }

    public List<CdoSnapshot> getHistoricals(GlobalId globalId, LocalDateTime timePoint, boolean withChildValueObjects, int limit) {
        argumentsAreNotNull(globalId, timePoint);

        return delegate.getStateHistory(globalId, QueryParamsBuilder
                .withLimit(limit)
                .withChildValueObjects(withChildValueObjects)
                .to(timePoint).build());

    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        argumentsAreNotNull(queryParams);

        return delegate.getSnapshots(queryParams);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        argumentIsNotNull(snapshotIdentifiers);

        return delegate.getSnapshots(snapshotIdentifiers);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        return delegate.getStateHistory(givenClasses, queryParams);
    }

    @Override
    public void persist(Commit commit) {
        delegate.persist(commit);
    }

    @Override
    public CommitId getHeadId() {
        return delegate.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
    }

    @Override
    public void ensureSchema() {
        delegate.ensureSchema();
    }

    private List<Change> filterChangesByPropertyNames(List<Change> changes, final QueryParams queryParams) {
        if (queryParams.changedProperties().size() == 0){
            return changes;
        }

        return Lists.positiveFilter(changes, input -> input instanceof PropertyChange &&
                queryParams.changedProperties().contains(((PropertyChange) input).getPropertyName()));
    }

    private List<Change> getChangesIntroducedBySnapshots(List<CdoSnapshot> snapshots) {
        return snapshotDiffer.calculateDiffs(snapshots, previousSnapshotsCalculator.calculate(snapshots));
    }

    //required for the corner case, when valueObject snapshots consume all the limit
    private List<CdoSnapshot> loadMasterEntitySnapshotIfNecessary(InstanceId instanceId, List<CdoSnapshot> alreadyLoaded) {
        if (alreadyLoaded.isEmpty()) {
            return alreadyLoaded;
        }

        if (alreadyLoaded.stream().filter(s -> s.getGlobalId().equals(instanceId)).findFirst().isPresent()) {
            return alreadyLoaded;
        }

        return getLatest(instanceId).map(it -> {
            List<CdoSnapshot> enhanced = new ArrayList(alreadyLoaded);
            enhanced.add(it);
            return java.util.Collections.unmodifiableList(enhanced);
        }).orElse(alreadyLoaded);
    }
}
