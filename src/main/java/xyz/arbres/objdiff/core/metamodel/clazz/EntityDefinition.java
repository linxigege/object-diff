package xyz.arbres.objdiff.core.metamodel.clazz;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.metamodel.type.EntityType;

import java.util.List;


/**
 * Recipe for {@link EntityType}
 *
 * @author bartosz walacik
 * @see EntityDefinitionBuilder
 */
public class EntityDefinition extends ClientsClassDefinition {
    private final List<String> idPropertyNames;
    private final boolean shallowReference;

    /**
     * Recipe for Entity with Id-property selected by @Id annotation
     */
    public EntityDefinition(Class<?> entity) {
        this(new EntityDefinitionBuilder(entity));
    }

    /**
     * Recipe for Entity with Id-property selected explicitly by name
     */
    public EntityDefinition(Class<?> entity, String idPropertyName) {
        this(new EntityDefinitionBuilder(entity)
                .withIdPropertyName(idPropertyName));
    }

    public EntityDefinition(Class<?> entity, String idPropertyName, List<String> ignoredProperties) {
        this(new EntityDefinitionBuilder(entity)
                .withIdPropertyName(idPropertyName)
                .withIgnoredProperties(ignoredProperties));
    }

    EntityDefinition(EntityDefinitionBuilder builder) {
        super(builder);
        this.idPropertyNames = Lists.immutableCopyOf(builder.getIdPropertyNames());
        this.shallowReference = builder.isShallowReference();
    }

    public boolean hasExplicitId() {
        return idPropertyNames.size() > 0;
    }

    /**
     * @return an immutable, non-null list
     */
    public List<String> getIdPropertyNames() {
        return idPropertyNames;
    }

    public boolean isShallowReference() {
        return shallowReference;
    }
}
