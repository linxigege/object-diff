package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.common.collections.Primitives;
import xyz.arbres.objdiff.core.diff.appenders.HashWrapper;
import xyz.arbres.objdiff.core.metamodel.property.MissingProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * Atomic
 *
 * @author carlos
 * @date 2021-12-08
 */
public class Atomic implements Serializable {
    private final Object value;

    public Atomic(Object value) {
        this.value = value instanceof HashWrapper ? ((HashWrapper)value).unwrap() : value;
    }

    public boolean isNull() {
        return value == null;
    }

    /**
     * @return true if value is not null and is primitive, box or String
     */
    public boolean isJsonBasicType() {
        if(isNull()) {
            return false;
        }

        return Primitives.isJsonBasicType(value);
    }

    /**
     * original Value
     */
    public Object unwrap() {
        return MissingProperty.INSTANCE == value ? null : value;
    }

    @Override
    public String toString() {
        return "value:"+value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Atomic)) {
            return false;
        }

        Atomic other = (Atomic)obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }
}
