package xyz.arbres.objdiff.core.metamodel.clazz;


import xyz.arbres.objdiff.core.metamodel.type.CustomType;
import xyz.arbres.objdiff.core.metamodel.type.EntityType;
import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;
import xyz.arbres.objdiff.core.metamodel.type.ValueType;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Recipe for {@link EntityType}, {@link ValueObjectType}, {@link ValueType} or {@link CustomType}
 *
 * @author bartosz walacik
 */
public abstract class ClientsClassDefinition {
    private final Class<?> baseJavaClass;
    private final Optional<String> typeName;
    private final PropertiesFilter propertiesFilter;

    ClientsClassDefinition(Class<?> baseJavaClass) {
        this(baseJavaClass, emptyList(), Optional.empty(), emptyList());
    }

    ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties) {
        this(baseJavaClass, ignoredProperties, Optional.empty(), emptyList());
    }

    ClientsClassDefinition(ClientsClassDefinitionBuilder builder) {
        this(builder.getClazz(), builder.getIgnoredProperties(), builder.getTypeName(), builder.getIncludedProperties());
    }

    private ClientsClassDefinition(Class<?> baseJavaClass, List<String> ignoredProperties, Optional<String> typeName, List<String> includedProperties) {


        this.baseJavaClass = baseJavaClass;
        this.typeName = typeName;
        this.propertiesFilter = new PropertiesFilter(includedProperties, ignoredProperties);
    }

    public Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o || getClass() != o.getClass()) {
            return false;
        }

        ClientsClassDefinition that = (ClientsClassDefinition) o;

        return baseJavaClass.equals(that.baseJavaClass);
    }

    @Override
    public int hashCode() {
        return baseJavaClass.hashCode();
    }

    public Optional<String> getTypeName() {
        return typeName;
    }

    public boolean hasTypeName() {
        return typeName.isPresent();
    }

    public PropertiesFilter getPropertiesFilter() {
        return propertiesFilter;
    }
}
