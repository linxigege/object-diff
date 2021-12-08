package xyz.arbres.objdiff.common.reflection;

import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * ObjDiffField
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ObjDiffField extends ObjDiffMember<Field> {
    
    protected ObjDiffField(Field rawField, Type resolvedReturnType){
        super(rawField,resolvedReturnType);
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericType();
    }

    @Override
    public Class<?> getRawType() {
        return getRawMember().getType();
    }

    @Override
    public Object getEvenIfPrivate(Object onObject) {
        try {
            return getRawMember().get(onObject);
        } catch (IllegalArgumentException ie) {
            return getOnMissingProperty(onObject);
        } catch (IllegalAccessException e) {
            throw new ObjDiffException(ObjDiffExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public void setEvenIfPrivate(Object onObject, Object value) {
        try {
            getRawMember().set(onObject, value);
        } catch (IllegalArgumentException ie){
            String valueType = value == null ? "null" : value.getClass().getName();
            throw new ObjDiffException(ObjDiffExceptionCode.PROPERTY_SETTING_ERROR, valueType, this, ie.getClass().getName() + " - " + ie.getMessage());
        } catch (IllegalAccessException e) {
            throw new ObjDiffException(ObjDiffExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public String memberType() {
        return "Field";
    }
}
