package xyz.arbres.objdiff.core.snapshot;


import xyz.arbres.objdiff.core.graph.ObjectNode;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.EnumerableType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.Collections;
import java.util.List;

class SnapshotNode extends ObjectNode<CdoSnapshot> {

    public SnapshotNode(CdoSnapshot cdo) {
        super(cdo);
    }

    @Override
    public GlobalId getReference(Property property) {

        Object propertyValue = getPropertyValue(property);
        if (propertyValue instanceof GlobalId) {
            return (GlobalId) propertyValue;
        } else {
            //when user's class is refactored, a property can have different type
            return null;
        }
    }

    @Override
    protected Object getDehydratedPropertyValue(String property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public Object getDehydratedPropertyValue(ObjDiffProperty property) {
        return getCdo().getPropertyValue(property);
    }

    @Override
    public List<GlobalId> getReferences(ObjDiffProperty property) {
        if (property.getType() instanceof EnumerableType) {
            Object propertyValue = getPropertyValue(property);
            EnumerableType enumerableType = property.getType();
            return enumerableType.filterToList(propertyValue, GlobalId.class);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isEdge() {
        return getCdo().isTerminal();
    }
}
