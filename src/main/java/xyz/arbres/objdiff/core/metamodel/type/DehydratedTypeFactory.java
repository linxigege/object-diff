package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 * Type for JSON representation. Generic version of {@link ClassType#getRawDehydratedType()}
 *
 * @author bartosz.walacik
 */
class DehydratedTypeFactory {
    private static Class GLOBAL_ID_ARRAY_TYPE = new GlobalId[]{}.getClass();

    private TypeMapper mapper;

    DehydratedTypeFactory(TypeMapper mapper) {
        this.mapper = mapper;
    }

    //recursive
    public Type build(Type givenType) {
        if (givenType instanceof TypeVariable) {
            return Object.class;
        }
        final ClassType ObjDiffType = mapper.getObjDiffClassType(givenType);

        //for Generics, we have list of type arguments to dehydrate
        if (ObjDiffType.isGenericType()) {
            List<Type> actualDehydratedTypeArguments = extractAndDehydrateTypeArguments(ObjDiffType);
            return new ParametrizedDehydratedType(ObjDiffType.getBaseJavaClass(), actualDehydratedTypeArguments);
        }

        if (ObjDiffType instanceof ArrayType) {
            Type dehydratedItemType = build(ObjDiffType.getConcreteClassTypeArguments().get(0));
            if (dehydratedItemType == GlobalId.class) {
                return GLOBAL_ID_ARRAY_TYPE;
            }
            return givenType;
        }

        return ObjDiffType.getRawDehydratedType();
    }

    private List<Type> extractAndDehydrateTypeArguments(ObjDiffType genericType) {
        return Lists.transform(genericType.getConcreteClassTypeArguments(), typeArgument -> build(typeArgument));
    }
}
