package xyz.arbres.objdiff.core.diff;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.object.InstanceId;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Change
 *
 * @author carlos
 * @date 2021-12-07
 */
public abstract class Change implements Serializable {

    private final CommitMetadata commitMetadata; //optional, can't use Optional here, because it isn't Serializable
    private final GlobalId affectedCdoId;
    private transient Object affectedCdo;  //optional

    protected Change(GlobalId affectedCdoId, Optional<Object> affectedCdo) {
        this(affectedCdoId, affectedCdo, Optional.empty());
    }

    protected Change(GlobalId affectedCdoId, Optional<Object> affectedCdo, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(affectedCdoId, affectedCdo, commitMetadata);
        this.affectedCdoId = affectedCdoId;
        affectedCdo.ifPresent(cdo -> this.affectedCdo = cdo);
        this.commitMetadata = commitMetadata.orElse(null);
    }

    /**
     * Affected domain object GlobalId
     */
    public GlobalId getAffectedGlobalId() {
        return affectedCdoId;
    }

    /**
     * Affected domain object local Id (value under @Id property)
     */
    public Object getAffectedLocalId() {
        if (affectedCdoId instanceof InstanceId){
            return ((InstanceId) affectedCdoId).getCdoId();
        }
        return null;
    }

    /**
     * Affected domain object.
     * Depending on concrete Change type,
     * it could be a new Object, removed Object or a new version of a changed Object.
     * <br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from ObjDiffRepository
     */
    public Optional<Object> getAffectedObject() {
        return Optional.ofNullable(affectedCdo);
    }

    /**
     * Empty if change is calculated by {@link ObjDiff#compare(Object, Object)}
     */
    public Optional<CommitMetadata> getCommitMetadata() {
        return Optional.ofNullable(commitMetadata);
    }

    /**
     * Pretty print with default dates formatting
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ " +prettyPrint(PrettyValuePrinter.getDefault()) +" }";
    }

    public abstract String prettyPrint(PrettyValuePrinter valuePrinter);

    void setAffectedCdo(Object affectedCdo) {
        Validate.argumentIsNotNull(affectedCdo);
        Validate.conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Change) {
            Change that = (Change) obj;
            return Objects.equals(this.affectedCdoId, that.affectedCdoId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.affectedCdoId);
    }
}
