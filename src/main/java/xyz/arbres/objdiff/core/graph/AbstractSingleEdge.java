package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

abstract class AbstractSingleEdge extends Edge {
    AbstractSingleEdge(ObjDiffProperty property) {
        super(property);
    }

    abstract GlobalId getReference();

    public Object getDehydratedPropertyValue() {
        return getReference();
    }
}
