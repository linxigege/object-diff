package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.common.collections.Maps;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.map.*;
import xyz.arbres.objdiff.core.metamodel.type.MapType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;
import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bartosz walacik
 */
class MapChangeAppender implements PropertyChangeAppender<MapChange> {

    @Override
    public boolean supports(ObjDiffType propertyType) {
        if (!(propertyType instanceof MapType)){
            return false;
        }

        MapType mapType = (MapType)propertyType;
        if (mapType.getKeyObjDiffType() instanceof ValueObjectType){
            throw new ObjDiffException(ObjDiffExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY, propertyType);
        }

        return true;
    }

    @Override
    public MapChange calculateChanges(NodePair pair, ObjDiffProperty property) {
        MapType mapType = property.getType();

        Map left =  wrapKeysIfNeeded((Map) pair.getLeftDehydratedPropertyValueAndSanitize(property), mapType.getKeyObjDiffType());
        Map right = wrapKeysIfNeeded((Map) pair.getRightDehydratedPropertyValueAndSanitize(property), mapType.getKeyObjDiffType());

        List<EntryChange> changes = calculateEntryChanges(left, right, mapType.getValueObjDiffType());

        if (!changes.isEmpty()){
            CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded(mapType.getKeyJavaType(), "key", "Map", property);
            CorePropertyChangeAppender.renderNotParametrizedWarningIfNeeded(mapType.getValueJavaType(), "value", "Map", property);
            return new MapChange(pair.createPropertyChangeMetadata(property), changes,
                    (Map)pair.getLeftPropertyValueAndSanitize(property),
                    (Map)pair.getRightPropertyValueAndSanitize(property));
        }
        else {
            return null;
        }
    }

    private Map wrapKeysIfNeeded(Map map, ObjDiffType mapKeyType) {
        return HashWrapper.wrapKeysIfNeeded(map, mapKeyType);
    }

    /**
     * @return never returns null
     */
    List<EntryChange> calculateEntryChanges(Map leftMap, Map rightMap, ObjDiffType mapValueType) {

        List<EntryChange> changes = new ArrayList<>();

        for (Object commonKey : Maps.commonKeys(leftMap, rightMap)) {
            Object leftVal  = leftMap.get(commonKey);
            Object rightVal = rightMap.get(commonKey);

            if (!mapValueType.equals(leftVal, rightVal)){
                changes.add( new EntryValueChange(commonKey, leftVal, rightVal));
            }
        }

        for (Object addedKey : Maps.keysDifference(rightMap, leftMap)) {
            Object addedValue  = rightMap.get(addedKey);
            changes.add( new EntryAdded(addedKey, addedValue));
        }

        for (Object removedKey : Maps.keysDifference(leftMap, rightMap)) {
            Object removedValue  = leftMap.get(removedKey);
            changes.add( new EntryRemoved(removedKey, removedValue));
        }

        return changes;
    }
}
