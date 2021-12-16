package xyz.arbres.objdiff.core.metamodel.clazz;


import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

import java.util.List;

/**
 * Recipe for {@link ValueObjectType}
 *
 * @author bartosz walacik
 * @see ValueObjectDefinitionBuilder
 */
public class ValueObjectDefinition extends ClientsClassDefinition {
    private final boolean defaultType;

    /**
     * Simple recipe for ValueObject
     */
    public ValueObjectDefinition(Class<?> valueObject) {
        super(valueObject);
        this.defaultType = false;
    }

    /**
     * Recipe for ValueObject with ignoredProperties
     */
    public ValueObjectDefinition(Class<?> valueObject, List<String> ignoredProperties) {
        super(valueObject, ignoredProperties);
        this.defaultType = false;
    }

    ValueObjectDefinition(ValueObjectDefinitionBuilder builder) {
        super(builder);
        this.defaultType = builder.isDefault();

    }

    public boolean isDefault() {
        return defaultType;
    }
}
