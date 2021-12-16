package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerElementChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.diff.changetype.map.EntryChange;
import xyz.arbres.objdiff.core.metamodel.type.CollectionType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.List;


abstract class ListToMapAppenderAdapter extends CorePropertyChangeAppender<ListChange> {
    private final MapChangeAppender mapChangeAppender;

    ListToMapAppenderAdapter(MapChangeAppender mapChangeAppender) {
        this.mapChangeAppender = mapChangeAppender;
    }

    ListChange calculateChangesInList(List leftList, List rightList, NodePair pair, ObjDiffProperty property) {
        CollectionType listType = property.getType();

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(Lists.asMap(leftList), Lists.asMap(rightList), listType.getItemObjDiffType());

        if (!entryChanges.isEmpty()) {
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            renderNotParametrizedWarningIfNeeded(listType.getItemJavaType(), "item", "List", property);
            return new ListChange(pair.createPropertyChangeMetadata(property), elementChanges, leftList, rightList);
        } else {
            return null;
        }
    }
}
