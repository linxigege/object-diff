package xyz.arbres.objdiff.core.diff.changetype.container;


import xyz.arbres.objdiff.common.collections.Arrays;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;
import java.util.Objects;

/**
 * Changes on an Array property
 *
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange<Object> {

    public ArrayChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Object left, Object right) {
        super(metadata, changes, left, right);
    }

    public ArrayChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes, null, null);
    }

    /**
     * size of right (or old) Array at {@link #getRight()}
     */
    @Override
    public int getRightSize() {
        return Arrays.length(getRight());
    }

    /**
     * size of left (or old) Array at {@link #getLeft()}
     */
    @Override
    public int getLeftSize() {
        return Arrays.length(getLeft());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ArrayChange) {
            ArrayChange that = (ArrayChange) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
