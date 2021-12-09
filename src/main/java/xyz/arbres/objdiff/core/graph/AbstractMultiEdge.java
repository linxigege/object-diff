package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

abstract class AbstractMultiEdge extends Edge {
    public AbstractMultiEdge(ObjDiffProperty property) {
        super(property);
    }
}
