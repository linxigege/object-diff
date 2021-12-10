package xyz.arbres.objdiff.core.diff.changetype;



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
