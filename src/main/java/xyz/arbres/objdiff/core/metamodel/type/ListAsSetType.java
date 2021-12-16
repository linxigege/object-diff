package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.EnumerableFunction;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class ListAsSetType extends CollectionType {

    public ListAsSetType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, typeMapperLazy);
    }

    @Override
    public boolean isInstance(Object cdo) {
        return cdo instanceof Set || cdo instanceof List;
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        return super.mapToSet(sourceEnumerable, mapFunction, owner);
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return List.class;
    }
}
