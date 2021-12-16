package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.ValueChange;
import xyz.arbres.objdiff.core.diff.changetype.map.ValueChangeFactory;
import xyz.arbres.objdiff.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
class ValueChangeAppender implements PropertyChangeAppender<ValueChange> {

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof PrimitiveOrValueType || propertyType instanceof TokenType;
    }

    /**
     * @param property supported property (of PrimitiveType or ValueObjectType)
     */
    @Override
    public ValueChange calculateChanges(NodePair pair, ObjDiffProperty property) {

        Object leftValue = pair.getLeftPropertyValue(property);
        Object rightValue = pair.getRightPropertyValue(property);

        //special treatment for EmbeddedId - could be ValueObjects without good equals() implementation
        if (isIdProperty(pair, property)) {
            //For idProperty, only initial change is possible (from null to value).
            //If we have values on both sides, we know that they have the same String representation
            if (leftValue != null && rightValue != null) {
                return null;
            }
        } else {
            if (property.getType().equals(leftValue, rightValue)) {
                return null;
            }
        }

        return ValueChangeFactory.create(pair, property, leftValue, rightValue);
    }

    private boolean isIdProperty(NodePair nodePair, ObjDiffProperty property) {
        ManagedType managedType = nodePair.getManagedType();

        if (managedType instanceof EntityType) {
            return ((EntityType) managedType).isIdProperty(property);
        }
        return false;
    }
}
