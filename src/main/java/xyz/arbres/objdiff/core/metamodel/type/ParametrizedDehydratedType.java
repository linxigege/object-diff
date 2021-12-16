package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.string.ToStringBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;


/**
 * @author bartosz walacik
 */
public class ParametrizedDehydratedType implements ParameterizedType {
    private final Type rawType;
    private final Type[] actualTypeArguments;

    public ParametrizedDehydratedType(Type rawType, List<Type> actualDehydratedTypeArguments) {

        this.rawType = rawType;
        this.actualTypeArguments = actualDehydratedTypeArguments.toArray(new Type[]{});
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public String toString() {
        return rawType + ToStringBuilder.listToString(Lists.asList(actualTypeArguments));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParametrizedDehydratedType that = (ParametrizedDehydratedType) o;

        return rawType.equals(that.rawType) && Arrays.equals(actualTypeArguments, that.actualTypeArguments);
    }

    @Override
    public int hashCode() {
        return rawType.hashCode() + 31 * Arrays.hashCode(actualTypeArguments);
    }
}
