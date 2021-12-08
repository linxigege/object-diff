package xyz.arbres.objdiff.core.metamodel.type;



import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * Maps Java types into ObjDiff types
 *
 * @author bartosz walacik
 */
public class TypeMapper implements TypeMapperLazy {
    static final ValueType OBJECT_TYPE = new ValueType(Object.class);

    private final TypeMapperEngine engine;
    private final TypeFactory typeFactory;

    private final DehydratedTypeFactory dehydratedTypeFactory = new DehydratedTypeFactory(this);

    public TypeMapper(ClassScanner classScanner, CoreConfiguration ObjDiffCoreConfiguration, DynamicMappingStrategy dynamicMappingStrategy) {
        //Pico doesn't support cycles, so manual construction
        TypeFactory typeFactory = new TypeFactory(classScanner, this, dynamicMappingStrategy);
        this.typeFactory = typeFactory;
        this.engine = new TypeMapperEngine(this, ObjDiffCoreConfiguration);
    }

    /**
     * For TypeMapperConcurrentTest only,
     * no better idea how to writhe this test without additional constructor
     */
    @Deprecated
    protected TypeMapper(TypeFactory typeFactory, TypeMapperEngine engine) {
        this.typeFactory = typeFactory;
        this.engine = engine;
    }

    /**
     * is Set, List or Array of ManagedClasses
     */
    public boolean isContainerOfManagedTypes(ObjDiffType ObjDiffType){
        if (! (ObjDiffType instanceof ContainerType)) {
            return false;
        }

        return ((ContainerType)ObjDiffType).getItemObjDiffType() instanceof ManagedType;
    }

    /**
     * is Map (or Multimap) with ManagedClass on Key or Value position
     */
    public boolean isKeyValueTypeWithManagedTypes(ObjDiffType enumerableType) {
        if (enumerableType instanceof KeyValueType){
            KeyValueType mapType = (KeyValueType)enumerableType;

            ObjDiffType keyType = mapType.getKeyObjDiffType();
            ObjDiffType valueType = mapType.getValueObjDiffType();

            return keyType instanceof ManagedType ||
                   valueType instanceof ManagedType ||
                   isContainerOfManagedTypes(valueType);
        } else{
            return false;
        }
    }

    public boolean isEnumerableOfManagedTypes(ObjDiffType ObjDiffType){
        return isContainerOfManagedTypes(ObjDiffType) || isKeyValueTypeWithManagedTypes(ObjDiffType);
    }

    /**
     * Returns mapped type, spawns a new one from a prototype,
     * or infers a new one using default mapping.
     */
    public ObjDiffType getObjDiffType(Type javaType) {

        if (javaType == Object.class) {
            return OBJECT_TYPE;
        }

        return engine.computeIfAbsent(javaType, j -> typeFactory.infer(j, findPrototype(j)));
    }

    public boolean isShallowReferenceType(Type javaType) {
        return getObjDiffType(javaType) instanceof ShallowReferenceType;
    }

    public ClassType getObjDiffClassType(Type javaType) {
        ObjDiffType jType = getObjDiffType(javaType);

        if (jType instanceof ClassType) {
            return (ClassType) jType;
        }

        throw new ObjDiffException(ObjDiffExceptionCode.CLASS_MAPPING_ERROR,
                    javaType,
                    jType.getClass().getSimpleName(),
                    ClassType.class.getSimpleName());
    }

    /**
     * @throws ObjDiffException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public ManagedType getObjDiffManagedType(GlobalId globalId){
        return getObjDiffManagedType(engine.getClassByTypeName(globalId.getTypeName()), ManagedType.class);
    }

    /**
     * @throws ObjDiffException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public <T extends ManagedType> T getObjDiffManagedType(String typeName, Class<T> expectedType) {
        return getObjDiffManagedType(engine.getClassByTypeName(typeName), expectedType);
    }

    /**
     * for tests only
     */
    private <T extends ManagedType> T getObjDiffManagedType(String typeName) {
        return (T)getObjDiffManagedType(engine.getClassByTypeName(typeName), ManagedType.class);
    }

    /**
     * @throws ObjDiffException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public <T extends ManagedType> T getObjDiffManagedType(DuckType duckType, Class<T> expectedType) {
        return getObjDiffManagedType(engine.getClassByDuckType(duckType), expectedType);
    }

    /**
     * If given javaClass is mapped to ManagedType, returns its ObjDiffType
     *
     * @throws ObjDiffException MANAGED_CLASS_MAPPING_ERROR
     */
    public ManagedType getObjDiffManagedType(Class javaType) {
        return getObjDiffManagedType(javaType, ManagedType.class);
    }

    /**
     * If given javaClass is mapped to expected ManagedType, returns its ObjDiffType
     *
     * @throws ObjDiffException MANAGED_CLASS_MAPPING_ERROR
     */
    public <T extends ManagedType> T getObjDiffManagedType(Class javaClass, Class<T> expectedType) {
        ObjDiffType mType = getObjDiffType(javaClass);

        if (expectedType.isAssignableFrom(mType.getClass())) {
            return (T) mType;
        } else {
            throw new ObjDiffException(ObjDiffExceptionCode.MANAGED_CLASS_MAPPING_ERROR,
                    javaClass,
                    mType.getClass().getSimpleName(),
                    expectedType.getSimpleName());
        }
    }

    public <T extends ManagedType> Optional<T> getObjDiffManagedTypeMaybe(String typeName, Class<T> expectedType) {
        return getObjDiffManagedTypeMaybe(new DuckType(typeName), expectedType);
    }

    public <T extends ManagedType> Optional<T> getObjDiffManagedTypeMaybe(DuckType duckType, Class<T> expectedType) {
        try {
            return Optional.of(getObjDiffManagedType(duckType, expectedType));
        } catch (ObjDiffException e) {
            if (ObjDiffExceptionCode.TYPE_NAME_NOT_FOUND == e.getCode()) {
                return Optional.empty();
            }
            if (ObjDiffExceptionCode.MANAGED_CLASS_MAPPING_ERROR == e.getCode()) {
                return Optional.empty();
            }
            throw e;
        }
    }

    public <T extends ObjDiffType> T getPropertyType(Property property){
        argumentIsNotNull(property);
        try {
            return (T) getObjDiffType(property.getGenericType());
        }catch (ObjDiffException e) {
            logger.error("Can't calculate ObjDiffType for property: {}", property);
            throw e;
        }
    }

    public void registerClientsClass(ClientsClassDefinition def) {
        ObjDiffType newType = typeFactory.create(def);

        logger.debug("ObjDiffType of '{}' " + "mapped explicitly to {}",
                def.getBaseJavaClass().getSimpleName(), newType.getClass().getSimpleName());

        engine.registerExplicitType(newType);
    }

    /**
     * Dehydrated type for JSON representation
     */
    public Type getDehydratedType(Type type) {
        return dehydratedTypeFactory.build(type);
    }

    public void addPluginTypes(Collection<ObjDiffType> jTypes) {
        Validate.argumentIsNotNull(jTypes);
        for (ObjDiffType t : jTypes) {
            engine.registerExplicitType(t);
        }
    }

    boolean contains(Type javaType){
        return engine.contains(javaType);
    }

    private Optional<ObjDiffType> findPrototype(Type javaType) {
        if (javaType instanceof TypeVariable) {
            return Optional.empty();
        }

        Class javaClass = extractClass(javaType);

        //this is due too spoiled Java Array reflection API
        if (javaClass.isArray()) {
            return Optional.of(getObjDiffType(Object[].class));
        }

        ObjDiffType selfClassType = engine.get(javaClass);
        if (selfClassType != null && javaClass != javaType){
            return  Optional.of(selfClassType); //returns rawType for ParametrizedTypes
        }

        List<Type> hierarchy = ReflectionUtil.calculateHierarchyDistance(javaClass);

        for (Type parent : hierarchy) {
            ObjDiffType jType = engine.get(parent);
            if (jType != null && jType.canBePrototype()) {
                logger.debug("proto for {} -> {}", javaType, jType);
                return Optional.of(jType);
            }
        }

        return Optional.empty();
    }
}
