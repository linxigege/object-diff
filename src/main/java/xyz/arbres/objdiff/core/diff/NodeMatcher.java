package xyz.arbres.objdiff.core.diff;


import jdk.nashorn.internal.ir.ObjectNode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.*;

/**
 * @author bartosz walacik
 */
class NodeMatcher {
    /**
     * matching based on {@link org.ObjDiff.core.metamodel.object.GlobalId}
     */
    public List<NodePair> match(GraphPair graphPair) {
        Validate.argumentIsNotNull(graphPair);

        List<NodePair> pairs = new ArrayList<>();
        Map<GlobalId, ObjectNode> rightMap = asMap(graphPair.getRightNodeSet());

        for (ObjectNode left : graphPair.getLeftNodeSet()) {
            GlobalId key = left.getGlobalId();
            if (rightMap.containsKey(key)) {
                pairs.add(new NodePair(left, rightMap.get(key), graphPair.getCommitMetadata()));
            }
        }

        return pairs;
    }

    private Map<GlobalId, ObjectNode> asMap(Set<ObjectNode> nodes) {
        Map<GlobalId, ObjectNode> map = new HashMap<>();

        for (ObjectNode node : nodes) {
            map.put(node.getGlobalId(),node);
        }

        return map;
    }
}
