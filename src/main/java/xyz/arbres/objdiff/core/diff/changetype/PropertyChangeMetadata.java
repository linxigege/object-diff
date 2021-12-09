package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.Optional;
import static xyz.arbres.objdiff.common.validation.Validate.argumentsAreNotNull;

/**
 * PropertyChangeMetadata
 *
 * @author carlos
 * @date 2021-12-08
 */
public class PropertyChangeMetadata {

    private final GlobalId affectedCdoId;
    private final String propertyName;
    private final Optional<CommitMetadata> commitMetadata;
    private final PropertyChangeType changeType;

    public PropertyChangeMetadata(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata, PropertyChangeType changeType) {
        argumentsAreNotNull(affectedCdoId, propertyName, commitMetadata, changeType);
        this.affectedCdoId = affectedCdoId;
        this.propertyName = propertyName;
        this.commitMetadata = commitMetadata;
        this.changeType = changeType;
    }

    public GlobalId getAffectedCdoId() {
        return affectedCdoId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    public PropertyChangeType getChangeType() {
        return changeType;
    }
}
