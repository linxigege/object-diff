package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.reflection.ObjDiffField;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
class TailoredObjDiffFieldFactory extends TailoredObjDiffMemberFactory {

    @Override
    public ObjDiffField create(final Property primaryProperty, final Class<?> genericItemClass) {
        return new ObjDiffField((Field) primaryProperty.getMember().getRawMember(), null) {
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
