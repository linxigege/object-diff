package xyz.arbres.objdiff.core.diff.changetype;

public enum PropertyChangeType {

    /**
     * When a property of the right object is absent in the left object.
     */
    PROPERTY_ADDED,

    /**
     * When a property of the left object is absent in the right object.
     */
    PROPERTY_REMOVED,

    /**
     * Regular value change &mdash; when a property is present in both objects.
     */
    PROPERTY_VALUE_CHANGED
}
