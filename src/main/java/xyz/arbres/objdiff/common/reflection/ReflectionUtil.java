package xyz.arbres.objdiff.common.reflection;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.collections.Sets;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.ObjDiff;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static java.util.Collections.unmodifiableSet;

/**
 * ReflectionUtil
 *
 * @author carlos
 * @date 2021-12-07
 */
public class ReflectionUtil {
    public static boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, ObjDiff.class.getClassLoader());
            return true;
        }
        catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

    public static boolean isAnnotationPresentInHierarchy(Class<?> clazz, Class<? extends Annotation> ann){
        Class<?> current = clazz;

        while (current != null && current != Object.class){
            if (current.isAnnotationPresent(ann)){
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    static boolean isNotStatic(Member member) {
        return !Modifier.isStatic(member.getModifiers());
    }
    /**
     * for example: Map<String, String> -> Map
     */
    public static Class extractClass(Type javaType) {
        if (javaType instanceof ParameterizedType
                && ((ParameterizedType)javaType).getRawType() instanceof Class){
            return (Class)((ParameterizedType)javaType).getRawType();
        }  else if (javaType instanceof GenericArrayType) {
            return Object[].class;
        }  else if (javaType instanceof Class) {
            return (Class)javaType;
        }

        throw new ObjDiffException(ObjDiffExceptionCode.CLASS_EXTRACTION_ERROR, javaType);
    }

    public static List<ObjDiffGetter> getAllGetters(Class methodSource) {
        ObjDiffGetterFactory getterFactory = new ObjDiffGetterFactory(methodSource);
        return getterFactory.getAllGetters();
    }

    public static List<ObjDiffField> getAllFields(Class<?> methodSource) {
        ObjDiffFieldFactory fieldFactory = new ObjDiffFieldFactory(methodSource);
        return fieldFactory.getAllFields();
    }

    public static Optional<ObjDiffMember> getMirrorMember(ObjDiffMember member, Class methodSource) {
        if (member instanceof ObjDiffGetter) {
            return (Optional)getMirrorGetter((ObjDiffGetter)member, methodSource);
        }
        if (member instanceof ObjDiffField) {
            return (Optional)getMirrorField((ObjDiffField)member, methodSource);
        }
        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
    }

    public static Optional<ObjDiffField> getMirrorField(ObjDiffField field, Class methodSource) {
        return getAllFields(methodSource).stream().filter(f -> f.propertyName().equals(field.propertyName())).findFirst();
    }

    public static Optional<ObjDiffGetter> getMirrorGetter(ObjDiffGetter getter, Class methodSource) {
        return getAllGetters(methodSource).stream().filter(f -> f.propertyName().equals(getter.propertyName())).findFirst();
    }

    public static boolean looksLikeId(Member member) {
        return getAnnotations(member).stream()
                .map(ann -> ann.annotationType().getSimpleName())
                .anyMatch(annName -> annName.equals(Property.ID_ANN) || annName.equals(Property.EMBEDDED_ID_ANN));
    }

    public static Set<Annotation> getAnnotations(Member member) {
        return unmodifiableSet(Sets.asSet(((AccessibleObject) member).getAnnotations()));
    }

    public static boolean isAssignableFromAny(Class clazz, List<Class<?>> assignableFrom) {
        for (Class<?> standardPrimitive : assignableFrom) {
            if (standardPrimitive.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static Optional<Type> isConcreteType(Type javaType){
        if (javaType instanceof Class || javaType instanceof ParameterizedType) {
            return Optional.of(javaType);
        } else if (javaType instanceof WildcardType) {
            // If the wildcard type has an explicit upper bound (i.e. not Object), we use that
            WildcardType wildcardType = (WildcardType) javaType;
            if (wildcardType.getLowerBounds().length == 0) {
                for (Type type : wildcardType.getUpperBounds()) {
                    if (type instanceof Class && ((Class<?>) type).equals(Object.class)) {
                        continue;
                    }
                    return Optional.of(type);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Makes sense for {@link ParameterizedType}
     */
    public static List<Type> getAllTypeArguments(Type javaType) {
        if (!(javaType instanceof ParameterizedType)) {
            return Collections.emptyList();
        }

        return Lists.immutableListOf(((ParameterizedType) javaType).getActualTypeArguments());
    }

    public static String reflectiveToString(Object obj) {
        Validate.argumentIsNotNull(obj);

        StringBuilder ret = new StringBuilder();
        for (ObjDiffField f : getAllPersistentFields(obj.getClass()) ){
            Object val = f.getEvenIfPrivate(obj);
            if (val != null) {
                ret.append(val.toString());
            }
            ret.append(",");
        }

        if (ret.length() == 0) {
            return obj.toString();
        }
        else{
            ret.delete(ret.length()-1, ret.length());
            return ret.toString();
        }
    }
    private static boolean isPersistentField(Field field) {
        return !Modifier.isTransient(field.getModifiers()) &&
                !Modifier.isStatic(field.getModifiers()) &&
                !"this$0".equals(field.getName()); //owner of inner class
    }

    public static List<ObjDiffField> getAllPersistentFields(Class methodSource) {
        List<ObjDiffField> result = new ArrayList<>();
        for(ObjDiffField field : getAllFields(methodSource)) {
            if (isPersistentField(field.getRawMember())) {
                result.add(field);
            }
        }
        return result;
    }
    public static <T> T getAnnotationValue(Annotation ann, String propertyName) {
        return (T) ReflectionUtil.invokeGetter(ann, propertyName);
    }

    public static Object invokeGetter(Object target, String getterName) {
        Validate.argumentsAreNotNull(target, getterName);
        try {
            Method m = target.getClass().getMethod(getterName);
            return m.invoke(target);
        }catch (Exception e ) {
            throw new ObjDiffException(e);
        }
    }

    public static List<Type> calculateHierarchyDistance(Class<?> clazz) {
        List<Type> interfaces = new ArrayList<>();

        List<Type> parents = new ArrayList<>();

        Class<?> current = clazz;
        while (current != null && current != Object.class){
            if (clazz != current) {
                parents.add(current);
            }

            for (Class i : current.getInterfaces()) {
                if (!interfaces.contains(i)) {
                    interfaces.add(i);
                }
            }

            current = current.getSuperclass();
        }

        parents.addAll(interfaces);

        return parents;
    }
}
