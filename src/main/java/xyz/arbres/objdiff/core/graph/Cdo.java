package xyz.arbres.objdiff.core.graph;

import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;

import java.util.Optional;

public abstract class Cdo {

    private final ManagedType managedType;

    protected Cdo(ManagedType managedType) {
        Validate.argumentsAreNotNull(managedType);
        this.managedType = managedType;
    }

    public abstract GlobalId getGlobalId();

    public abstract Optional<Object> getWrappedCdo();

    public abstract boolean isNull(Property property);

    public abstract Object getPropertyValue(Property property);

    public abstract Object getPropertyValue(String propertyName);

    @Override
    public String toString() {
        return getGlobalId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cdo)) {
            return false;
        }

        Cdo other = (Cdo) o;
        return getGlobalId().equals(other.getGlobalId());
    }

    @Override
    public int hashCode() {
        return getGlobalId().hashCode();
    }

    public ManagedType getManagedType() {
        return managedType;
    }
}
