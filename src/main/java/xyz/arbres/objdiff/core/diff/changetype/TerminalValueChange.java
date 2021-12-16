package xyz.arbres.objdiff.core.diff.changetype;


import xyz.arbres.objdiff.core.ObjDiffBuilder;
import xyz.arbres.objdiff.core.metamodel.type.PrimitiveOrValueType;

/**
 * TerminalValueChange is a subtype of ValueChange with a property value on left and null on right.
 * It is generated for each Primitive or Value property of a Removed Object to capture its state.
 *
 * @see ObjectRemoved
 * @see PrimitiveOrValueType
 * @see ObjDiffBuilder#withTerminalChanges(boolean)
 */
public class TerminalValueChange extends ValueChange {

    public TerminalValueChange(PropertyChangeMetadata metadata, Object leftValue) {
        super(metadata, leftValue, null);
    }
}
