package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.EnumerableFunction;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.EnumerationAwareOwnerContext;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public class ListType extends CollectionType {

    public ListType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, typeMapperLazy);
    }

    /**
     * @return immutable List
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);
        List sourceList = Lists.wrapNull(sourceEnumerable);
        List targetList = new ArrayList(sourceList.size());

        EnumerationAwareOwnerContext enumerationContext = new IndexableEnumerationOwnerContext(owner);
        for (Object sourceVal : sourceList) {
            targetList.add(sourceVal == null ? null : mapFunction.apply(sourceVal, enumerationContext));
        }
        return unmodifiableList(targetList);
    }

    @Override
    public Object map(Object sourceEnumerable, Function mapFunction, boolean filterNulls) {
        List sourceCol = Lists.wrapNull(sourceEnumerable);
        return unmodifiableList((List) sourceCol.stream()
                .map(sourceVal -> sourceVal == null ? null : mapFunction.apply(sourceVal))
                .filter(mappedVal -> !filterNulls || mappedVal != null)
                .collect(toList()));
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return List.class;
    }
}
