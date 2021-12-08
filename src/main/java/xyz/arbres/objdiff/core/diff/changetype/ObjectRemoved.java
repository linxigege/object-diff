package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.Optional;

public final class ObjectRemoved extends Change {
    ObjectRemoved(GlobalId removed, Optional<Object> removedCdo) {
        this(removed, removedCdo, Optional.empty());
    }

    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo, Optional<CommitMetadata> commitMetadata) {
        super(removed, removedCdo, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ObjectRemoved) {
            ObjectRemoved that = (ObjectRemoved) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);
        return "object removed: " + getAffectedGlobalId().value();
    }
}