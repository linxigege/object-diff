package xyz.arbres.objdiff.core.diff.changetype.container;

import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChangeMetadata;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class SetChange extends CollectionChange<Set<?>> {
    public SetChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Set left, Set right) {
        super(metadata, changes, left, right);
        for (ContainerElementChange change: changes){
            Validate.conditionFulfilled(change instanceof ValueAddOrRemove, "SetChange constructor failed, expected ValueAddOrRemove");
            Validate.conditionFulfilled(change.getIndex() == null, "SetChange constructor failed, expected empty change.index");
        }
    }

    public SetChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes, Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SetChange) {
            SetChange that = (SetChange) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}