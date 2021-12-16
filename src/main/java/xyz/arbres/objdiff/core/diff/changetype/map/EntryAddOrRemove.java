package xyz.arbres.objdiff.core.diff.changetype.map;


import xyz.arbres.objdiff.common.string.ToStringBuilder;
import xyz.arbres.objdiff.core.diff.changetype.Atomic;

import java.util.Objects;

/**
 * Entry added or removed from a Map
 *
 * @author bartosz walacik
 */
public abstract class EntryAddOrRemove extends EntryChange {
    private final Atomic value;

    EntryAddOrRemove(Object key, Object value) {
        super(key);
        this.value = new Atomic(value);
    }

    public Object getValue() {
        return value.unwrap();
    }

    public Atomic getWrappedValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, getKey(), getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof EntryAddOrRemove) {
            EntryAddOrRemove that = (EntryAddOrRemove) obj;
            return super.equals(that)
                    && Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
