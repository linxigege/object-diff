package xyz.arbres.objdiff.core.diff.appenders.levenshtein;


import xyz.arbres.objdiff.core.diff.EqualsFunction;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.appenders.CorePropertyChangeAppender;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerElementChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.metamodel.type.ListType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.List;

/**
 * @author kornel kielczewski
 */
public class LevenshteinListChangeAppender extends CorePropertyChangeAppender<ListChange> {

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ObjDiffProperty property) {
        ObjDiffType itemType = ((ListType) property.getType()).getItemObjDiffType();

        final List leftList = (List) leftValue;
        final List rightList = (List) rightValue;

        EqualsFunction equalsFunction = itemType::equals;
        Backtrack backtrack = new Backtrack(equalsFunction);
        StepsToChanges stepsToChanges = new StepsToChanges(equalsFunction);

        final BacktrackSteps[][] steps = backtrack.evaluateSteps(leftList, rightList);
        final List<ContainerElementChange> changes = stepsToChanges.convert(steps, leftList, rightList);

        ListChange result = createListChange(pair, property, changes, leftList, rightList);
        if (result != null) {
            renderNotParametrizedWarningIfNeeded(itemType.getBaseJavaType(), "item", "List", property);
        }
        return result;
    }

    private ListChange createListChange(NodePair pair, ObjDiffProperty property, List<ContainerElementChange> changes, List left, List right) {
        final ListChange result;

        if (changes.isEmpty()) {
            result = null;
        } else {
            result = new ListChange(pair.createPropertyChangeMetadata(property), changes, left, right);
        }
        return result;
    }
}
