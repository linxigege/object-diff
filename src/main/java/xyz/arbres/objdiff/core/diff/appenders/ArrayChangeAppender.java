package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.common.collections.Arrays;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.container.ArrayChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerElementChange;
import xyz.arbres.objdiff.core.diff.changetype.map.EntryChange;
import xyz.arbres.objdiff.core.metamodel.type.ArrayType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.List;
import java.util.Map;

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppender implements PropertyChangeAppender<ArrayChange>{
    private final MapChangeAppender mapChangeAppender;

    ArrayChangeAppender(MapChangeAppender mapChangeAppender) {
        this.mapChangeAppender = mapChangeAppender;
    }

    @Override
    public boolean supports(ObjDiffType propertyType) {
        return  propertyType instanceof ArrayType;
    }

    @Override
    public ArrayChange calculateChanges(NodePair pair, ObjDiffProperty property) {

        Map leftMap =  Arrays.asMap(pair.getLeftDehydratedPropertyValueAndSanitize(property));
        Map rightMap = Arrays.asMap(pair.getRightDehydratedPropertyValueAndSanitize(property));

        ArrayType arrayType = property.getType();

        List<EntryChange> entryChanges =
                mapChangeAppender.calculateEntryChanges(leftMap, rightMap, arrayType.getItemObjDiffType());

        if (!entryChanges.isEmpty()){
            List<ContainerElementChange> elementChanges = Lists.transform(entryChanges, new MapChangesToListChangesFunction());
            return new ArrayChange(pair.createPropertyChangeMetadata(property), elementChanges,
                pair.getLeftPropertyValueAndSanitize(property),
                pair.getRightPropertyValueAndSanitize(property));
        }
        else {
            return null;
        }
    }
}
