package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.reflection.ObjDiffMember;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.ParametrizedDehydratedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
abstract class TailoredObjDiffMemberFactory {

    protected abstract ObjDiffMember create(Property primaryProperty, Class<?> genericItemClass);

    protected ParameterizedType parametrizedType(Property property, Class<?> itemClass) {
        return new ParametrizedDehydratedType(property.getRawType(), Lists.asList((Type) itemClass));
    }
}