package xyz.arbres.objdiff.core.diff;


import xyz.arbres.objdiff.common.collections.Sets;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.graph.ObjectGraph;
import xyz.arbres.objdiff.core.diff.graph.ObjectNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;


/**
 * @author bartosz walacik
 */
public class GraphPair {

    private final ObjectGraph leftGraph;
    private final ObjectGraph rightGraph;

    private final Collection<ObjectNode> onlyOnLeft;
    private final Collection<ObjectNode> onlyOnRight;

    private final Optional<CommitMetadata> commitMetadata;

    GraphPair(ObjectGraph leftGraph, ObjectGraph rightGraph) {
        this(leftGraph, rightGraph, Optional.empty());
    }

    public GraphPair(ObjectGraph leftGraph, ObjectGraph rightGraph, Optional<CommitMetadata> commitMetadata) {
        this.leftGraph = leftGraph;
        this.rightGraph = rightGraph;

        Function<ObjectNode, Integer> hasher = objectNode -> objectNode.cdoHashCode();

        this.onlyOnLeft = Sets.difference(leftGraph.nodes(), rightGraph.nodes(), hasher);
        this.onlyOnRight = Sets.difference(rightGraph.nodes(), leftGraph.nodes(), hasher);

        this.commitMetadata = commitMetadata;
    }

    //for initial
    public GraphPair(ObjectGraph currentGraph) {
        this.leftGraph = new EmptyGraph();

        this.rightGraph = currentGraph;

        this.onlyOnLeft = Collections.emptySet();
        this.onlyOnRight = rightGraph.nodes();

        this.commitMetadata = Optional.empty();
    }

    public Collection<ObjectNode> getOnlyOnLeft() {
        return onlyOnLeft;
    }

    public Collection<ObjectNode> getOnlyOnRight() {
        return onlyOnRight;
    }

    public Set<ObjectNode> getLeftNodeSet() {
        return leftGraph.nodes();
    }

    public Set<ObjectNode> getRightNodeSet() {
        return rightGraph.nodes();
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }
}
