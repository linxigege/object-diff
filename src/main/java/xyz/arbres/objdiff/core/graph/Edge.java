package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.List;

/**
 * Relation between (Entity) instances
 * <br>
 * Immutable
 *
 * @author bartosz walacik
 */
abstract class Edge {
    private final ObjDiffProperty property;

    Edge(ObjDiffProperty property) {
        Validate.argumentIsNotNull(property);
        this.property = property;
    }

    ObjDiffProperty getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Edge that = (Edge) obj;
        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    abstract List<LiveNode> getReferences();

    abstract Object getDehydratedPropertyValue();
}
