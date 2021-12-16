package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.ReferenceChange;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;
import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

import java.util.Objects;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
class ReferenceChangeAppender implements PropertyChangeAppender<ReferenceChange> {

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof ManagedType && !(propertyType instanceof ValueObjectType);
    }

    @Override
    public ReferenceChange calculateChanges(NodePair pair, ObjDiffProperty property) {
        GlobalId leftId = pair.getLeftReference(property);
        GlobalId rightId = pair.getRightReference(property);

        if (Objects.equals(leftId, rightId)) {
            return null;
        }

        return new ReferenceChange(pair.createPropertyChangeMetadata(property), leftId, rightId,
                pair.getLeftPropertyValue(property),
                pair.getRightPropertyValue(property));
    }
}
