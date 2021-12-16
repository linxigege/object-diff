package xyz.arbres.objdiff.common.collections;


import xyz.arbres.objdiff.core.metamodel.object.EnumerationAwareOwnerContext;

@FunctionalInterface
public interface EnumerableFunction<F, T> {
    T apply(F input, EnumerationAwareOwnerContext ownerContext);
}
