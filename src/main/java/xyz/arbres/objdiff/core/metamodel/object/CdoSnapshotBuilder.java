package xyz.arbres.objdiff.core.metamodel.object;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author bartosz walacik
 */
public class CdoSnapshotBuilder {
    private GlobalId globalId;
    private CommitMetadata commitMetadata;
    private SnapshotType type = SnapshotType.UPDATE;
    private CdoSnapshotState state;
    private CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
    private CdoSnapshot previous;
    private boolean markAllAsChanged;
    private List<String> changed = Collections.emptyList();
    private ManagedType managedType;
    private long version;

    private CdoSnapshotBuilder() {
    }

    static CdoSnapshotBuilder emptyCopyOf(CdoSnapshot snapshot) {
        return cdoSnapshot()
                .withGlobalId(snapshot.getGlobalId())
                .withManagedType(snapshot.getManagedType())
                .withCommitMetadata(snapshot.getCommitMetadata())
                .withType(snapshot.getType());
    }

    public static CdoSnapshotBuilder cdoSnapshot() {
        return new CdoSnapshotBuilder();
    }

    public CdoSnapshotBuilder withGlobalId(GlobalId globalId) {
        this.globalId = globalId;
        return this;
    }

    public CdoSnapshotBuilder withManagedType(ManagedType managedType) {
        this.managedType = managedType;
        return this;
    }

    public CdoSnapshotBuilder withCommitMetadata(CommitMetadata commitMetadata) {
        this.commitMetadata = commitMetadata;
        return this;
    }

    public CdoSnapshotBuilder withState(CdoSnapshotState state) {
        Validate.argumentIsNotNull(state);
        this.state = state;
        return this;
    }

    public CdoSnapshotBuilder withVersion(Long version) {
        this.version = (version == null) ? 0 : version;
        return this;
    }

    public CdoSnapshot build() {
        if (state == null) {
            state = stateBuilder.build();
        }

        if (previous != null) {
            changed = state.differentValues(previous.getState());
        }

        if (markAllAsChanged) {
            changed = new ArrayList<>(state.getPropertyNames());
        }

        return new CdoSnapshot(globalId, commitMetadata, state, type, changed, managedType, version);
    }

    public CdoSnapshotBuilder withType(SnapshotType type) {
        Validate.argumentIsNotNull(type);
        this.type = type;
        return this;
    }

    public CdoSnapshotBuilder markAllAsChanged() {
        markAllAsChanged = true;
        return this;
    }

    public CdoSnapshotBuilder withChangedProperties(List<String> changedPropertyNames) {
        changed = new ArrayList<>(changedPropertyNames);
        return this;
    }

    public CdoSnapshotBuilder markChanged(CdoSnapshot previous) {
        this.previous = previous;
        return this;
    }
}
