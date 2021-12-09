package xyz.arbres.objdiff.core.metamodel.object;

import xyz.arbres.objdiff.common.collections.Arrays;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.type.*;

import java.util.LinkedList;

/**
 * @author bartosz.walacik
 */
class GlobalIdPathParser {

    ValueObjectType parseChildValueObject(ManagedType ownerType, String path){
        return parseChildValueObjectFromPathSegments(ownerType, pathToSegments(path), path);
    }

    private ValueObjectType parseChildValueObjectFromPathSegments(ManagedType ownerType, LinkedList<String> segments, String path) {
        ObjDiffProperty property = ownerType.getProperty(segments.getFirst());

        ValueObjectType childVoType = extractChildValueObject(property.getType(), path);

        if (segments.size() == 1 ||
            segments.size() == 2 &&  property.getType() instanceof EnumerableType){
            return childVoType;
        }

        segments.removeFirst();
        if (property.getType() instanceof EnumerableType){
            segments.removeFirst(); //removing segment with list index or map key
        }

        return parseChildValueObjectFromPathSegments(childVoType, segments, path);
    }

    private ValueObjectType extractChildValueObject(ObjDiffType voPropertyType, String path) {

        if (voPropertyType instanceof ValueObjectType) {
            return (ValueObjectType) voPropertyType;
        }

        if (voPropertyType instanceof ContainerType) {
            ObjDiffType contentType  = ((ContainerType) voPropertyType).getItemObjDiffType();
            if (contentType instanceof ValueObjectType){
                return (ValueObjectType)contentType;
            }
        }

        if (voPropertyType instanceof MapType){
            ObjDiffType valueType  = ((MapType) voPropertyType).getValueObjDiffType();
            if (valueType instanceof ValueObjectType){
                return (ValueObjectType)valueType;
            }
        }

        throw new ObjDiffException(ObjDiffExceptionCode.CANT_EXTRACT_CHILD_VALUE_OBJECT,
                path, voPropertyType);

    }

    private LinkedList<String> pathToSegments(String path){
        Validate.argumentIsNotNull(path);
        return new LinkedList(Arrays.asList(path.split("/")));
    }
}
