package xyz.arbres.objdiff.core.diff.appenders;



import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.metamodel.type.CollectionType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.Collection;
import java.util.List;

class CollectionAsListChangeAppender extends ListToMapAppenderAdapter  {

    CollectionAsListChangeAppender(MapChangeAppender mapChangeAppender) {
        super(mapChangeAppender);
    }

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return propertyType.getClass() == CollectionType.class;
    }

    @Override
    public ListChange calculateChanges(Object leftValue, Object rightValue, NodePair pair, ObjDiffProperty property) {
        List leftList = Lists.immutableListOf((Collection)leftValue);
        List rightList = Lists.immutableListOf((Collection)rightValue);

        return super.calculateChangesInList(leftList, rightList, pair, property);
    }
}
