package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.Collections;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class ShallowSingleEdge extends AbstractSingleEdge {
    private final GlobalId reference;

    ShallowSingleEdge(ObjDiffProperty property, GlobalId referenced) {
        super(property);
        Validate.argumentIsNotNull(referenced);
        this.reference = referenced;
    }

    @Override
    GlobalId getReference() {
        return reference;
    }

    @Override
    List<LiveNode> getReferences() {
        return Collections.emptyList();
    }
}
