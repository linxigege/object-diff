package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.metamodel.object.ValueObjectId;

import java.util.Objects;
import java.util.Optional;

/**
 * PropertyChange
 *
 * @author carlos
 * @date 2021-12-08
 */
public abstract class PropertyChange<T> extends Change {

    private final PropertyChangeType changeType;
    private final String propertyName;

    protected PropertyChange(PropertyChangeMetadata propertyChangeMetadata) {
        super(propertyChangeMetadata.getAffectedCdoId(), Optional.empty(), propertyChangeMetadata.getCommitMetadata());
        this.propertyName = propertyChangeMetadata.getPropertyName();
        this.changeType = propertyChangeMetadata.getChangeType();
    }

    /**
     * Left (or old) value of a changed property
     */
    public abstract T getLeft();

    /**
     * Right (or new) value of a changed property
     */
    public abstract T getRight();

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyNameWithPath() {
        if (getAffectedGlobalId() instanceof ValueObjectId) {
            return ((ValueObjectId) getAffectedGlobalId()).getFragment() + "." + propertyName;
        }
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PropertyChange) {
            PropertyChange that = (PropertyChange) o;
            return super.equals(that) &&
                    Objects.equals(this.propertyName, that.propertyName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), propertyName);
    }

    /**
     * @see PropertyChangeType
     * @since 5.5.0
     */
    public PropertyChangeType getChangeType() {
        return changeType;
    }

    /**
     * @return <code>changeType == PropertyChangeType.PROPERTY_ADDED</code>
     * @see PropertyChangeType
     * @since 5.5.0
     */
    public boolean isPropertyAdded() {
        return changeType == PropertyChangeType.PROPERTY_ADDED;
    }

    /**
     * @return <code>changeType == PropertyChangeType.PROPERTY_REMOVED</code>
     * @see PropertyChangeType
     * @since 5.5.0
     */
    public boolean isPropertyRemoved() {
        return changeType == PropertyChangeType.PROPERTY_REMOVED;
    }

    /**
     * @return <code>changeType == PropertyChangeType.PROPERTY_VALUE_CHANGED</code>
     * @see PropertyChangeType
     * @since 5.5.0
     */
    public boolean isPropertyValueChanged() {
        return changeType == PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ property: '" + propertyName + "' }";
    }

}
