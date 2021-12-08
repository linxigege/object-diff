package xyz.arbres.objdiff.core.diff;



import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.graph.ObjectNode;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.property.MissingProperty;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * holds two versions of the same {@link ObjectNode}
 *
 * @author bartosz walacik
 */
public class NodePair {
    private final ObjectNode left;
    private final ObjectNode right;
    private final Optional<CommitMetadata> commitMetadata;

    NodePair(ObjectNode left, ObjectNode right) {
        this(left, right, Optional.empty());
    }

    public NodePair(ObjectNode left, ObjectNode right, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(left, right, commitMetadata);
        Validate.argumentCheck(left.getGlobalId().equals(right.getGlobalId()), "left & right should refer to the same Cdo");
        this.left = left;
        this.right = right;
        this.commitMetadata = commitMetadata;
    }

    public ManagedType getManagedType() {
        return right.getManagedType();
    }

    public boolean isNullOnBothSides(Property property) {
        return left.getPropertyValue(property) == null &&
                right.getPropertyValue(property) == null;
    }

    public Object getLeftPropertyValue(Property property) {
        return left.getPropertyValue(property);
    }

    public Object getRightPropertyValue(Property property) {
        return right.getPropertyValue(property);
    }

    public GlobalId getRightReference(Property property) {
        return right.getReference(property);
    }

    public GlobalId getLeftReference(Property property) {
        return left.getReference(property);
    }

    public List<GlobalId> getRightReferences(ObjDiffProperty property) {
        return right.getReferences(property);
    }

    public List<GlobalId> getLeftReferences(ObjDiffProperty property) {
        return left.getReferences(property);
    }

    public ObjectNode getRight() {
        return right;
    }

    public ObjectNode getLeft() {
        return left;
    }

    public ObjectNode getFirst() {
        return left;
    }

    boolean sameClass() {
        return right.getManagedType().getBaseJavaType() == left.getManagedType().getBaseJavaType();
    }

    public List<ObjDiffProperty> getProperties() {
        if (sameClass()) {
            return getManagedType().getProperties();
        }
        else {
            return Collections.unmodifiableList(getPropertiesFromBothSides());
        }
    }

    private List<ObjDiffProperty> getPropertiesFromBothSides() {
        Set<String> leftNames = left.getManagedType().getProperties().stream()
                .map(it -> it.getName()).collect(Collectors.toSet());


        return Stream.concat(left.getManagedType().getProperties().stream(),
                              right.getManagedType().getProperties().stream().filter(it -> !leftNames.contains(it.getName())))
                       .collect(Collectors.toList());
    }

    public GlobalId getGlobalId() {
        return left.getGlobalId();
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    public PropertyChangeType getChangeType(ObjDiffProperty property) {
        if (getLeft().getManagedType().getBaseJavaClass() == getRight().getManagedType().getBaseJavaClass()) {
            return PropertyChangeType.PROPERTY_VALUE_CHANGED;
        }

        if (getLeftPropertyValue(property) == MissingProperty.INSTANCE) {
            return PropertyChangeType.PROPERTY_ADDED;
        }

        if (getRightPropertyValue(property) == MissingProperty.INSTANCE) {
            return PropertyChangeType.PROPERTY_REMOVED;
        }

        return PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }

    public Object getRightDehydratedPropertyValueAndSanitize(ObjDiffProperty property) {
        return sanitize(getRight().getDehydratedPropertyValue(property), property.getType());
    }

    public Object getLeftDehydratedPropertyValueAndSanitize(ObjDiffProperty property) {
        return sanitize(getLeft().getDehydratedPropertyValue(property), property.getType());
    }

    public Object getLeftPropertyValueAndSanitize(ObjDiffProperty property) {
        return sanitize(getLeft().getPropertyValue(property), property.getType());
    }

    public Object getRightPropertyValueAndSanitize(ObjDiffProperty property) {
        return sanitize(getRight().getPropertyValue(property), property.getType());
    }

    public Object sanitize(Object value, ObjDiffType expectedType) {
        //all Enumerables (except Arrays) are sanitized
        if (expectedType instanceof EnumerableType && !(expectedType instanceof ArrayType)) {
            EnumerableType enumerableType = (EnumerableType)expectedType;
            if (value == null || !enumerableType.getEnumerableInterface().isAssignableFrom(value.getClass())) {
                return ((EnumerableType)expectedType).empty();
            }
        }
        return value;
    }

    public PropertyChangeMetadata createPropertyChangeMetadata(ObjDiffProperty property) {
        return new PropertyChangeMetadata(getGlobalId(), property.getName(), getCommitMetadata(), getChangeType(property));
    }
}
