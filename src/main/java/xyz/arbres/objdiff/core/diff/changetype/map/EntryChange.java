package xyz.arbres.objdiff.core.diff.changetype.map;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.diff.changetype.Atomic;

import java.io.Serializable;
import java.util.Objects;


/**
 * Any change in a Map
 *
 * @author bartosz walacik
 */
public abstract class EntryChange implements Serializable {
    private final Atomic key;

    EntryChange(Object key) {
        Validate.argumentIsNotNull(key);
        this.key = new Atomic(key);
    }

    public Object getKey() {
        return key.unwrap();
    }

    public Atomic getWrappedKey() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof EntryChange) {
            EntryChange that = (EntryChange) obj;
            return Objects.equals(this.key, that.key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), key);
    }

    protected abstract String prettyPrint(PrettyValuePrinter valuePrinter);
}
