package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.Optional;

/**
 * NewObject
 *
 * @author carlos
 * @date 2021-12-08
 */
public final class NewObject extends Change {

    NewObject(GlobalId newId, Optional<Object> newCdo) {
        this(newId, newCdo, Optional.empty());
    }

    public NewObject(GlobalId newId, Optional<Object> newCdo, Optional<CommitMetadata> commitMetadata) {
        super(newId, newCdo, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NewObject) {
            NewObject that = (NewObject) obj;
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
        return "new object: " + getAffectedGlobalId().value();
    }
}
