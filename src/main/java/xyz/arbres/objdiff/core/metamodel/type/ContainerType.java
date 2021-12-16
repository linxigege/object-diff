package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.reflection.ReflectionUtil;

import java.lang.reflect.Type;


/**
 * Collection or Array
 *
 * @author bartosz walacik
 */
public abstract class ContainerType extends EnumerableType {

    ContainerType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, 1, typeMapperLazy);
    }

    public Type getItemJavaType() {
        return getConcreteClassTypeArguments().get(0);
    }

    public ObjDiffType getItemObjDiffType() {
        return getTypeMapperLazy().getObjDiffType(getItemJavaType());
    }

    /**
     * never returns null
     */
    public Class getItemClass() {
        return ReflectionUtil.extractClass(getItemJavaType());
    }
}
