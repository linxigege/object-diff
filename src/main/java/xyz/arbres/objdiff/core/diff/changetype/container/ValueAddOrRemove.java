package xyz.arbres.objdiff.core.diff.changetype.container;



import xyz.arbres.objdiff.core.diff.changetype.Atomic;

import java.util.Objects;

/**
 * Abstract change in a collection
 *
 * @author bartosz walacik
 */
public abstract class ValueAddOrRemove extends ContainerElementChange {
    final Atomic value;

    ValueAddOrRemove(Object value) {
        this.value = new Atomic(value);
    }

    ValueAddOrRemove(Integer index, Object value) {
        super(index);
        this.value = new Atomic(value);
    }

    /**
     * For collections of Primitives or Values it's simply an added (or removed) item.<br/>
     * For collections of Entities or ValueObjects
     * it's a {@link GlobalId} reference to an added (or removed) item.
     */
    public Object getValue() {
        return value.unwrap();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ValueAddOrRemove) {
            ValueAddOrRemove that = (ValueAddOrRemove) obj;
            return super.equals(that)
                    && Objects.equals(this.getValue(), that.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getValue());
    }

}
