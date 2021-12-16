package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.object.ValueObjectId;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.EnumerableType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class LiveNode extends ObjectNode<LiveCdo> {

    private final Map<String, Edge> edges = new HashMap<>();

    public LiveNode(LiveCdo cdo) {
        super(cdo);
    }

    Edge getEdge(Property property) {
        return getEdge(property.getName());
    }

    Edge getEdge(String propertyName) {
        return edges.get(propertyName);
    }

    @Override
    public boolean isEdge() {
        return false;
    }

    @Override
    public GlobalId getReference(Property property) {
        Edge edge = getEdge(property);

        if (edge instanceof AbstractSingleEdge) {
            return ((AbstractSingleEdge) edge).getReference();
        } else {
            //when user's class is refactored, a property can have different type
            return null;
        }
    }

    @Override
    public List<GlobalId> getReferences(ObjDiffProperty property) {
        Edge edge = getEdge(property); //could be null for snapshots

        if (edge != null) {
            return edge.getReferences()
                    .stream()
                    .map(it -> it.getGlobalId())
                    .collect(toList());
        } else {
            //when user's class is refactored, a collection can contain different items
            return Collections.emptyList();
        }
    }

    @Override
    protected Object getDehydratedPropertyValue(String propertyName) {
        return getManagedType().findProperty(propertyName)
                .map(p -> getDehydratedPropertyValue(p))
                .orElse(null);
    }

    /**
     * Enumerables are copied to new structures (immutable when possible)
     */
    @Override
    public Object getDehydratedPropertyValue(ObjDiffProperty property) {
        Edge edge = getEdge(property);

        if (edge != null) {
            return edge.getDehydratedPropertyValue();
        }

        Object propertyValue = getCdo().getPropertyValue(property);
        if (propertyValue == null) {
            return null;
        }

        //Collections & Maps are copied to a new immutable structure
        if (property.getType() instanceof EnumerableType) {
            EnumerableType enumerableType = property.getType();

            return enumerableType.map(propertyValue, it -> it);
        }

        return getCdo().getPropertyValue(property);
    }

    void addEdge(Edge edge) {
        this.edges.put(edge.getProperty().getName(), edge);
    }

    Set<LiveCdo> descendants(int maxDepth) {
        return new NodeTraverser(this, maxDepth, null).descendantsSet();
    }

    List<LiveCdo> descendantVOs(int maxDepth) {
        return new NodeTraverser(this, maxDepth,
                (LiveNode n) -> n.getGlobalId() instanceof ValueObjectId).descendantsList();
    }

    @Override
    public String toString() {
        return "LiveNode{" + hashCode() + ", globaId:" + getGlobalId() +
                ", edges:" + edges.size() + " }";
    }

    private static class NodeTraverser {
        private final List<LiveCdo> descendantsList = new ArrayList<>();
        private final int maxDepth;
        private final ObjectNode root;
        private final Predicate<LiveNode> filter;

        NodeTraverser(LiveNode root, int maxDepth, Predicate<LiveNode> filter) {
            this.maxDepth = maxDepth;
            this.root = root;
            this.filter = filter != null ? filter : (LiveNode n) -> true;
            followEdges(root, 1);
        }

        void follow(Edge edge, int depth) {
            edge.getReferences().forEach(n -> {
                if (!n.equals(root) && filter.test(n)) {
                    descendantsList.add(n.getCdo());
                    if (depth < maxDepth) {
                        followEdges(n, depth + 1);
                    }
                }
            });
        }

        List<LiveCdo> descendantsList() {
            return Lists.immutableListOf(descendantsList);
        }

        Set<LiveCdo> descendantsSet() {
            return Collections.unmodifiableSet(new HashSet<>(descendantsList));
        }

        void followEdges(LiveNode node, int depth) {
            node.edges.values().forEach(e -> follow((Edge) e, depth));
        }
    }
}
