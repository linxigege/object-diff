package xyz.arbres.objdiff.core.diff.changetype.map;


import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.InitialValueChange;
import xyz.arbres.objdiff.core.diff.changetype.TerminalValueChange;
import xyz.arbres.objdiff.core.diff.changetype.ValueChange;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

public class ValueChangeFactory {
    public static ValueChange create(NodePair pair, ObjDiffProperty property, Object left, Object right) {
        if (pair.getLeft().isEdge()) {
            return new InitialValueChange(pair.createPropertyChangeMetadata(property), right);
        }
        if (pair.getRight().isEdge()) {
            return new TerminalValueChange(pair.createPropertyChangeMetadata(property), left);
        }
        return new ValueChange(pair.createPropertyChangeMetadata(property), left, right);
    }
}
