package xyz.arbres.objdiff.common.reflection;

import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * ObjDiffGetter
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ObjDiffGetter extends ObjDiffMember<Method> {

    private static final Object[] EMPTY_ARRAY = new Object[]{};

    private final Optional<Method> setterMethod;

    protected ObjDiffGetter(Method getterMethod, Type resolvedReturnType) {
        super(getterMethod, resolvedReturnType);
        setterMethod = findSetterForGetter(getterMethod);
    }

    protected ObjDiffGetter(Method getterMethod, Type resolvedReturnType, boolean looksLikeId) {
        super(getterMethod, resolvedReturnType, looksLikeId);
        setterMethod = findSetterForGetter(getterMethod);
    }

    private static String setterNameForGetterName(String getterName) {
        return "set" + getterNameWithoutPrefix(getterName);
    }

    private static String getterNameWithoutPrefix(String getterName) {
        if (getterName.substring(0, 3).equals("get")) {
            return getterName.substring(3);
        }

        if (getterName.substring(0, 2).equals("is")) {
            return getterName.substring(2);
        }

        throw new IllegalArgumentException("Name {" + getterName + "} is not a getter name");
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericReturnType();
    }

    @Override
    public Class<?> getRawType() {
        return getRawMember().getReturnType();
    }

    @Override
    public Object getEvenIfPrivate(Object onObject) {
        try {
            return getRawMember().invoke(onObject, EMPTY_ARRAY);
        } catch (IllegalArgumentException ie) {
            return getOnMissingProperty(onObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ObjDiffException(ObjDiffExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName() + ": " + e.getMessage());
        }
    }

    @Override
    public void setEvenIfPrivate(Object onObject, Object value) {
        setterMethod.orElseThrow(() -> new ObjDiffException(ObjDiffExceptionCode.SETTER_NOT_FOUND,
                getRawMember().getName(), getRawMember().getDeclaringClass().getName()));

        try {
            setterMethod.get().invoke(onObject, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ObjDiffException(ObjDiffExceptionCode.SETTER_INVOCATION_ERROR,
                    setterMethod.get().getName(), onObject.getClass().getName(), e);
        }
    }

    @Override
    public String propertyName() {
        return getterNameToFieldName(name());
    }

    private Optional<Method> findSetterForGetter(Method getter) {
        Class<?> clazz = getter.getDeclaringClass();
        String setterName = setterNameForGetterName(getter.getName());
        try {
            Method setter = clazz.getDeclaredMethod(setterName, getter.getReturnType());
            setAccessibleIfNecessary(setter);
            return Optional.of(setter);
        } catch (NoSuchMethodException e) {

            return Optional.empty();
        }
    }

    private String getterNameToFieldName(String getterName) {
        String withoutPrefix = getterNameWithoutPrefix(getterName);
        return withoutPrefix.substring(0, 1).toLowerCase() + withoutPrefix.substring(1);
    }

    @Override
    public String memberType() {
        return "Getter";
    }
}
