package xyz.arbres.objdiff.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * for lazy type loading
 */
public interface TypeMapperLazy {
    ObjDiffType getObjDiffType(Type javaType);
}
