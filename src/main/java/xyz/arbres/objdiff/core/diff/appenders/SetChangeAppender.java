package xyz.arbres.objdiff.core.diff.appenders;



import xyz.arbres.objdiff.common.collections.Sets;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerElementChange;
import xyz.arbres.objdiff.core.diff.changetype.container.SetChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ValueAdded;
import xyz.arbres.objdiff.core.diff.changetype.container.ValueRemoved;
import xyz.arbres.objdiff.core.metamodel.type.*;

import java.util.*;

/**
 * @author pawel szymczyk
 */
class SetChangeAppender extends CorePropertyChangeAppender<SetChange> {

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof SetType;
    }

    @Override
    protected SetChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ObjDiffProperty property) {
        Set leftSet = wrapValuesIfNeeded(toSet(leftValue), property);
        Set rightSet = wrapValuesIfNeeded(toSet(rightValue), property);

        List<ContainerElementChange> entryChanges = calculateDiff(leftSet, rightSet);
        if (!entryChanges.isEmpty()) {
            CollectionType setType = property.getType();
            renderNotParametrizedWarningIfNeeded(setType.getItemJavaType(), "item", "Set", property);
            return new SetChange(pair.createPropertyChangeMetadata(property), entryChanges,
                    toSet(leftValue),
                    toSet(rightValue));
        } else {
            return null;
        }
    }

    private Set wrapValuesIfNeeded(Set set, ObjDiffProperty property) {
        ObjDiffType itemType = ((ContainerType)property.getType()).getItemObjDiffType();
        return HashWrapper.wrapValuesIfNeeded(set, itemType);
    }

    private Set toSet(Object collection) {
        if (collection instanceof Set) {
            return (Set) collection;
        }
        return new HashSet((Collection)collection);
    }

    private List<ContainerElementChange> calculateDiff(Set leftSet, Set rightSet) {
        if (Objects.equals(leftSet, rightSet)) {
            return Collections.emptyList();
        }

        List<ContainerElementChange> changes = new ArrayList<>();

        Sets.difference(leftSet, rightSet).forEach(valueOrId -> changes.add(new ValueRemoved(valueOrId)));

        Sets.difference(rightSet, leftSet).forEach(valueOrId -> changes.add(new ValueAdded(valueOrId)));

        return changes;
    }
}
