package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.List;

/**
 * OneToOne or ManyToOne relation
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
class SingleEdge extends AbstractSingleEdge {
    private final LiveNode referencedNode;

    SingleEdge(ObjDiffProperty property, LiveNode referencedNode) {
        super(property);
        Validate.argumentsAreNotNull(referencedNode);
        this.referencedNode = referencedNode;
    }

    @Override
    GlobalId getReference() {
        return referencedNode.getGlobalId();
    }

    @Override
    List<LiveNode> getReferences() {
        return Lists.asList(referencedNode);
    }
}
