package xyz.arbres.objdiff.core.diff.graph;


import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ObjectGraph<T extends Cdo> {
    private final Set<ObjectNode<T>> nodes;

    protected ObjectGraph(Set<ObjectNode<T>> nodes) {
        this.nodes = Collections.unmodifiableSet(nodes);
    }

    public Set<ObjectNode<T>> nodes() {
        return nodes;
    }

    public Set<T> cdos() {
        return nodes().stream()
                .map(node -> (T) node.getCdo())
                .collect(Collectors.toSet());
    }

    public Set<GlobalId> globalIds() {
        return nodes().stream()
                .map(ObjectNode::getGlobalId)
                .collect(Collectors.toSet());
    }

    public Optional<T> get(GlobalId globalId) {
        return nodes.stream()
                .filter(node -> globalId.equals(node.getGlobalId()))
                .findFirst()
                .map(node -> (T) node.getCdo());
    }
}
