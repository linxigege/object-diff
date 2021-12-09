package xyz.arbres.objdiff.core.diff.appenders;



import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.metamodel.type.ListType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class SimpleListChangeAppender extends ListToMapAppenderAdapter {

    SimpleListChangeAppender(MapChangeAppender mapChangeAppender) {
        super(mapChangeAppender);
    }

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType instanceof ListType;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ObjDiffProperty property) {
        List leftList = (List) leftValue;
        List rightList = (List) rightValue;

        return super.calculateChangesInList(leftList, rightList, pair, property);
    }
}
