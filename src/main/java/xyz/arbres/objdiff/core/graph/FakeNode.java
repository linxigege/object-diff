package xyz.arbres.objdiff.core.graph;



import xyz.arbres.objdiff.common.collections.Defaults;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.Collections;
import java.util.List;

public class FakeNode extends ObjectNode<Cdo>{

    public FakeNode(Cdo cdo) {
        super(cdo);
    }

    @Override
    public boolean isEdge() {
        return true;
    }

    @Override
    public GlobalId getReference(Property property) {
        return null;
    }

    @Override
    public List<GlobalId> getReferences(ObjDiffProperty property) {
        return Collections.emptyList();
    }

    @Override
    protected Object getDehydratedPropertyValue(String propertyName) {
        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED, "FakeLeftNodePair.getLeft()");
    }

    @Override
    public Object getPropertyValue(Property property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public Object getDehydratedPropertyValue(ObjDiffProperty property) {
        return Defaults.defaultValue(property.getGenericType());
    }

    @Override
    public boolean isNull(Property property) {
        return true;
    }
}
