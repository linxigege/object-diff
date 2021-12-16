package xyz.arbres.objdiff.core.diff.changetype;


import xyz.arbres.objdiff.core.ObjDiffBuilder;
import xyz.arbres.objdiff.core.metamodel.type.PrimitiveOrValueType;

/**
 * InitialValueChange is a subtype of ValueChange with null on left and a property value on right.
 * It is generated for each Primitive or Value property of a NewObject to capture its state.
 *
 * @see NewObject
 * @see PrimitiveOrValueType
 * @see ObjDiffBuilder#withInitialChanges(boolean)
 */
public class InitialValueChange extends ValueChange {
    public InitialValueChange(PropertyChangeMetadata metadata, Object rightValue) {
        super(metadata, null, rightValue);
    }
}
