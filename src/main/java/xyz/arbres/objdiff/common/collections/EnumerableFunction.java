package xyz.arbres.objdiff.common.collections;

import org.ObjDiff.core.metamodel.object.EnumerationAwareOwnerContext;

@FunctionalInterface
public interface EnumerableFunction<F,T> {
    T apply(F input, EnumerationAwareOwnerContext ownerContext);
}
