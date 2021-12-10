package xyz.arbres.objdiff.core.metamodel.type;



import xyz.arbres.objdiff.common.collections.Primitives;
import xyz.arbres.objdiff.common.collections.WellKnownValueTypes;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.CoreConfiguration;
import xyz.arbres.objdiff.core.diff.ListCompareAlgorithm;
import xyz.arbres.objdiff.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;



/**
 * thread-safe, mutable state of ObjDiffTypes mapping
 */
class TypeMapperEngine {

    private final Map<String, ObjDiffType> mappedTypes = new ConcurrentHashMap<>();
    private final Map<DuckType, Class> mappedTypeNames = new ConcurrentHashMap<>();
    private final TypeMapperLazy typeMapperlazy;

    TypeMapperEngine(TypeMapperLazy typeMapperlazy, CoreConfiguration ObjDiffCoreConfiguration) {
        this.typeMapperlazy = typeMapperlazy;
        this.registerCoreTypes(ObjDiffCoreConfiguration.getListCompareAlgorithm());
    }

    private void putIfAbsent(Type javaType, final ObjDiffType jType) {
        Validate.argumentsAreNotNull(javaType, jType);
        if (contains(javaType)) {
            return;
        }

        addFullMapping(javaType, jType);
    }

    private void putWithOverwrite(Type javaType, final ObjDiffType jType) {
        Validate.argumentsAreNotNull(javaType, jType);
        addFullMapping(javaType, jType);
    }

    void registerCoreTypes(ListCompareAlgorithm listCompareAlgorithm){
        //primitives & boxes
        for (Class primitiveOrBox : Primitives.getPrimitiveAndBoxTypes()) {
            registerCoreType(new PrimitiveType(primitiveOrBox));
        }

        registerCoreType(new PrimitiveType(Enum.class));

        //array
        registerCoreType(new ArrayType(Object[].class, typeMapperlazy));

        //well known Value types
        for (Class valueType : WellKnownValueTypes.getOldGoodValueTypes()) {
            registerCoreType(new ValueType(valueType));
        }

        //java util and sql types
        registerCoreTypes((List) UtilTypeCoreAdapters.valueTypes());



        //Collections
        registerCoreType(new CollectionType(Collection.class, typeMapperlazy));
        registerCoreType(new SetType(Set.class, typeMapperlazy));
        if (listCompareAlgorithm == ListCompareAlgorithm.AS_SET) {
            registerCoreType(new ListAsSetType(List.class, typeMapperlazy));
        } else {
            registerCoreType(new ListType(List.class, typeMapperlazy));
        }
        registerCoreType(new OptionalType(typeMapperlazy));

        //& Maps
        registerCoreType(new MapType(Map.class, typeMapperlazy));
    }

    void registerExplicitType(ObjDiffType ObjDiffType) {
        putWithOverwrite(ObjDiffType.getBaseJavaType(), ObjDiffType);
    }

    private void registerCoreType(ObjDiffType jType) {
        putIfAbsent(jType.getBaseJavaType(), jType);
    }

    private void registerCoreTypes(Collection<ObjDiffType> jTypes) {
        jTypes.forEach(t -> registerCoreType(t));
    }

    ObjDiffType computeIfAbsent(Type javaType, Function<Type, ObjDiffType> computeFunction) {
        ObjDiffType ObjDiffType = get(javaType);
        if (ObjDiffType != null) {
            return ObjDiffType;
        }

        synchronized (javaType) {
            //map.contains double check
            ObjDiffType mappedType = get(javaType);
            if (mappedType != null) {
                return mappedType;
            }

            ObjDiffType newType = computeFunction.apply(javaType);
            addFullMapping(javaType, newType);
            return newType;
        }
    }

    private void addFullMapping(Type javaType, ObjDiffType newType){
        Validate.argumentsAreNotNull(javaType, newType);

        mappedTypes.put(javaType.toString(), newType);

        if (newType instanceof ManagedType){
            ManagedType managedType = (ManagedType)newType;
            mappedTypeNames.put(new DuckType(managedType.getName()), ReflectionUtil.extractClass(javaType));
            mappedTypeNames.put(new DuckType(managedType), ReflectionUtil.extractClass(javaType));
        }
    }

    ObjDiffType get(Type javaType) {
        return mappedTypes.get(javaType.toString());
    }

    boolean contains(Type javaType) {
        return get(javaType) != null;
    }

    /**
     * @throws ObjDiffException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    Class getClassByTypeName(String typeName) {
        return getClassByDuckType(new DuckType(typeName));
    }

    /**
     * @throws ObjDiffException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    Class getClassByDuckType(DuckType duckType) {

        Class javaType = mappedTypeNames.get(duckType);
        if (javaType != null){
            return javaType;
        }

        synchronized (duckType.getTypeName()) {
            Optional<? extends Class> classForName = parseClass(duckType.getTypeName());
            if (classForName.isPresent()) {
                mappedTypeNames.put(duckType, classForName.get());
                return classForName.get();
            }
        }

        //try to fallback to bare typeName when properties doesn't match
        if (!duckType.isBare()){
            return getClassByDuckType(duckType.bareCopy());
        }

        throw new ObjDiffException(ObjDiffExceptionCode.TYPE_NAME_NOT_FOUND, duckType.getTypeName());
    }

    private Optional<? extends Class> parseClass(String qualifiedName){
        try {
            return Optional.of( this.getClass().forName(qualifiedName) );
        }
        catch (ClassNotFoundException e){
            return Optional.empty();
        }
    }

}
