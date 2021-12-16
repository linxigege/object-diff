package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.clazz.*;
import xyz.arbres.objdiff.core.metamodel.scanner.ClassScan;
import xyz.arbres.objdiff.core.metamodel.scanner.ClassScanner;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author bartosz walacik
 */
class TypeFactory {

    private final Map<Type, Hint> votes = new ConcurrentHashMap<>();

    private final ClassScanner classScanner;
    private final ManagedClassFactory managedClassFactory;
    private final EntityTypeFactory entityTypeFactory;

    private final DynamicMappingStrategy dynamicMappingStrategy;

    TypeFactory(ClassScanner classScanner, TypeMapper typeMapper, DynamicMappingStrategy dynamicMappingStrategy) {
        this.classScanner = classScanner;

        this.dynamicMappingStrategy = dynamicMappingStrategy;

        //Pico doesn't support cycles, so manual construction
        this.managedClassFactory = new ManagedClassFactory(typeMapper);

        this.entityTypeFactory = new EntityTypeFactory(managedClassFactory);
    }

    ObjDiffType create(ClientsClassDefinition def) {
        return create(def, null);
    }

    ObjDiffType create(ClientsClassDefinition def, ClassScan scanMaybe) {
        Supplier<ClassScan> lazyScan = () -> scanMaybe != null ? scanMaybe : classScanner.scan(def.getBaseJavaClass());

        if (def instanceof CustomDefinition) {
            return new CustomType(def.getBaseJavaClass(), ((CustomDefinition) def).getComparator());
        } else if (def instanceof EntityDefinition) {
            EntityType newType = entityTypeFactory.createEntity((EntityDefinition) def, lazyScan.get());
            saveHints(newType);
            return newType;
        } else if (def instanceof ValueObjectDefinition) {
            return createValueObject((ValueObjectDefinition) def, lazyScan.get());
        } else if (def instanceof ValueDefinition) {
            ValueDefinition valueDefinition = (ValueDefinition) def;
            return new ValueType(valueDefinition.getBaseJavaClass(),
                    valueDefinition.getComparator());
        } else if (def instanceof IgnoredTypeDefinition) {
            return new IgnoredType(def.getBaseJavaClass());
        } else {
            throw new IllegalArgumentException("unsupported definition " + def.getClass().getSimpleName());
        }
    }

    private void saveHints(EntityType newEntityType) {
        if (!newEntityType.hasCompositeId()) {
            votes.put(newEntityType.getIdProperty().getGenericType(), new EntityIdHint());
        }
    }

    private ValueObjectType createValueObject(ValueObjectDefinition definition, ClassScan scan) {
        return new ValueObjectType(managedClassFactory.create(definition, scan), definition.getTypeName(), definition.isDefault());
    }

    /**
     * for tests only
     */
    private ObjDiffType infer(Type javaType) {
        return infer(javaType, Optional.empty());
    }

    ObjDiffType infer(Type javaType, Optional<ObjDiffType> prototype) {

        Optional<ObjDiffType> tokenType = resolveIfTokenType(javaType);
        if (tokenType.isPresent()) {
            return tokenType.get();
        }

        final JavaRichType javaRichType = new JavaRichType(javaType);

        if (prototype.isPresent()) {
            ObjDiffType jType = spawnFromPrototype(javaRichType, prototype.get());

            return jType;
        }

        Optional<ObjDiffType> dynamicType = dynamicMappingStrategy.map(javaType);

        return dynamicType
                .orElseGet(() -> inferFromAnnotations(javaRichType)
                        .orElseGet(() -> inferFromHints(javaRichType)
                                .orElseGet(() -> createDefaultType(javaRichType))));
    }

    private Optional<ObjDiffType> resolveIfTokenType(Type javaType) {
        if (javaType instanceof TypeVariable) {
            return Optional.of(new TokenType((TypeVariable) javaType));
        }
        return Optional.empty();
    }

    private Optional<ObjDiffType> inferFromHints(JavaRichType richType) {
        Hint vote = votes.get(richType.javaType);

        if (vote != null) {
            ObjDiffType jType = vote.vote(richType);

            return Optional.of(jType);
        }

        return Optional.empty();
    }

    private ObjDiffType spawnFromPrototype(JavaRichType javaRichType, ObjDiffType prototype) {
        Validate.argumentsAreNotNull(javaRichType, prototype);

        if (prototype instanceof ManagedType) {
            ManagedType managedPrototype = (ManagedType) prototype;

            ManagedClass managedClass = managedClassFactory.createFromPrototype(javaRichType.javaClass, javaRichType.getScan(),
                    managedPrototype.getManagedClass().getManagedPropertiesFilter());
            return managedPrototype.spawn(managedClass, javaRichType.getScan().typeName());
        } else if (prototype instanceof CustomType) {
            CustomType customTypePrototype = (CustomType) prototype;
            return new CustomType(customTypePrototype.getBaseJavaType(), customTypePrototype.getComparator());
        } else {
            return prototype.spawn(javaRichType.javaType); //delegate to simple constructor
        }
    }

    private ObjDiffType createDefaultType(JavaRichType t) {
        return create(ValueObjectDefinitionBuilder.valueObjectDefinition(t.javaClass)
                .withTypeName(t.getScan().typeName())
                .defaultType()
                .build(), t.getScan());
    }

    private Optional<ObjDiffType> inferFromAnnotations(JavaRichType t) {
        if (t.getScan().hasValueAnn()) {
            return Optional.of(create(new ValueDefinition(t.javaClass), t.getScan()));
        }

        if (t.getScan().hasIgnoredAnn()) {
            return Optional.of(create(new IgnoredTypeDefinition(t.javaClass), t.getScan()));
        }

        if (t.getScan().hasValueObjectAnn()) {
            return Optional.of(create(ValueObjectDefinitionBuilder.valueObjectDefinition(t.javaClass).withTypeName(t.getAnnTypeName()).build(), t.getScan()));
        }

        if (t.getScan().hasShallowReferenceAnn()) {
            return Optional.of(create(EntityDefinitionBuilder.entityDefinition(t.javaClass).withTypeName(t.getAnnTypeName()).withShallowReference().build(), t.getScan()));
        }

        if (t.getScan().hasEntityAnn() || t.getScan().hasIdProperty()) {
            return Optional.of(create(EntityDefinitionBuilder.entityDefinition(t.javaClass).withTypeName(t.getAnnTypeName()).build(), t.getScan()));
        }

        return Optional.empty();
    }

    private interface Hint {
        ObjDiffType vote(JavaRichType richType);
    }

    private static class EntityIdHint implements Hint {
        @Override
        public ObjDiffType vote(JavaRichType richType) {
            return new ValueType(richType.javaType);
        }
    }

    private class JavaRichType {
        Supplier<ClassScan> classScan;
        private Type javaType;
        private Class javaClass;
        private ClassScan scan;

        JavaRichType(Type javaType) {
            this.javaType = javaType;
            this.javaClass = ReflectionUtil.extractClass(javaType);
            this.classScan = () -> classScanner.scan(javaClass);
        }

        String getTypeName() {
            return javaType.toString();
        }

        Object getSimpleName() {
            return javaClass.getSimpleName();
        }

        ClassScan getScan() {
            if (scan == null) {
                scan = classScan.get();
            }
            return scan;
        }

        Optional<String> getAnnTypeName() {
            return getScan().typeName();
        }
    }
}
