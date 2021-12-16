package xyz.arbres.objdiff.core.metamodel.object;

import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;

import java.io.Serializable;
import java.util.Comparator;

/**
 * GlobalId for client domain object cdo
 *
 * @author carlos
 * @date 2021-12-07
 */
public abstract class GlobalId implements Serializable, Comparable<GlobalId> {

    private final String typeName;

    GlobalId(String typeName) {
        Validate.argumentIsNotNull(typeName);
        this.typeName = typeName;
    }

    public abstract String value();

    @Override
    public String toString() {
        return this.value();
    }

    public boolean isTypeOf(ManagedType managedType) {
        return getTypeName().equals(managedType.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GlobalId)) {
            return false;
        }

        return value().equals(((GlobalId) o).value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    public String getTypeName() {
        return typeName;
    }

    public GlobalId masterObjectId() {
        return this;
    }

    String getTypeNameShort() {
        String[] split = getTypeName().split("\\.");
        if (split.length >= 2) {
            return "..." + split[split.length - 1];
        }
        return getTypeName();
    }

    @Override
    public int compareTo(GlobalId o) {
        return Comparator.comparing(GlobalId::value).compare(this, o);
    }
}
