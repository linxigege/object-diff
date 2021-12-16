package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.core.IgnoredClassesStrategy;

import java.lang.reflect.Type;
import java.util.Optional;

public class DynamicMappingStrategy {

    //nullable
    private final IgnoredClassesStrategy ignoredClassesStrategy;

    public DynamicMappingStrategy(IgnoredClassesStrategy ignoredClassesStrategy) {
        this.ignoredClassesStrategy = ignoredClassesStrategy;
    }

    public DynamicMappingStrategy() {
        this.ignoredClassesStrategy = null;
    }

    Optional<ObjDiffType> map(Type type) {
        if (ignoredClassesStrategy != null && type instanceof Class) {
            Class<?> clazz = (Class) type;
            if (ignoredClassesStrategy.isIgnored(clazz)) {
                return Optional.of(new IgnoredType(clazz));
            }
            return Optional.empty();
        }
        return Optional.empty();
    }
}
