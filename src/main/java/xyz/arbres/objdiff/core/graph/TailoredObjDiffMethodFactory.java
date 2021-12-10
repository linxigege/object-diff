package xyz.arbres.objdiff.core.graph;



import xyz.arbres.objdiff.common.reflection.ObjDiffGetter;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
class TailoredObjDiffMethodFactory extends TailoredObjDiffMemberFactory {

    @Override
    public ObjDiffGetter create(final Property primaryProperty, final Class<?> genericItemClass) {
        return new ObjDiffGetter((Method) primaryProperty.getMember().getRawMember(), null) {
            @Override
            public Type getGenericResolvedType() {
                return parametrizedType(primaryProperty, genericItemClass);
            }

            @Override
            protected Type getRawGenericType() {
                return genericItemClass;
            }
        };
    }
}
