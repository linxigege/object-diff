package xyz.arbres.objdiff.core.graph;



import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.Collections;
import java.util.List;

class ShallowMultiEdge extends AbstractMultiEdge {
    private final Object dehydratedPropertyValue;

    ShallowMultiEdge(ObjDiffProperty property, Object dehydratedPropertyValue) {
        super(property);
        this.dehydratedPropertyValue = dehydratedPropertyValue;
    }

    @Override
    List<LiveNode> getReferences() {
        return Collections.emptyList();
    }

    @Override
    Object getDehydratedPropertyValue() {
        return dehydratedPropertyValue;
    }
}
