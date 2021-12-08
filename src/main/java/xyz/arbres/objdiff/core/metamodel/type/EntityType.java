package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.string.PrettyPrintBuilder;
import xyz.arbres.objdiff.common.string.ToStringBuilder;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.InstanceId;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EntityType
 *
 * @author carlos
 * @date 2021-12-08
 */
public class EntityType extends ManagedType{

    private final List<ObjDiffProperty> idProperties;
    private final InstanceIdFactory instanceIdFactory;

    EntityType(ManagedClass entity, List<ObjDiffProperty> idProperties, Optional<String> typeName) {
        super(entity, typeName);
        Validate.argumentIsNotNull(idProperties);
        Validate.argumentCheck(idProperties.size() > 0, "no idProperties in " + entity.getBaseJavaClass());
        this.idProperties = Lists.immutableCopyOf(idProperties);
        this.instanceIdFactory = new InstanceIdFactory(this);
    }

    @Override
    EntityType spawn(ManagedClass managedClass, Optional<String> typeName) {
        //when spawning from prototype, prototype.idProperty and child.idProperty are different objects
        //with (possibly) different return types, so we need to update Id pointer
        return new EntityType(managedClass, managedClass.getProperties(getIdPropertyNames()), typeName);
    }

    /**
     * @return an immutable, non-null list with at least one element
     */
    public List<ObjDiffProperty> getIdProperties() {
        return idProperties;
    }

    /**
     * @throws RuntimeException if this Entity has Composite Id
     */
    public ObjDiffProperty getIdProperty() {
        Validate.conditionFulfilled(!hasCompositeId(), "getIdProperty() can't be called on Entity with Composite Id");
        return idProperties.get(0);
    }

    public boolean hasCompositeId() {
        return idProperties.size() > 1;
    }

    private List<String> getIdPropertyNames() {
        return idProperties.stream().map(Property::getName).collect(Collectors.toList());
    }

    public boolean isIdProperty(ObjDiffProperty property) {
        return idProperties.contains(property);
    }

    /**
     * @param instance instance of {@link #getBaseJavaClass()}
     * @return returns ID of given instance (value of idProperty)
     */
    public Object getIdOf(Object instance) {
        Validate.argumentIsNotNull(instance);

        if (!isInstance(instance)) {
            throw new ObjDiffException(ObjDiffExceptionCode.NOT_INSTANCE_OF,
                    getName(),
                    getBaseJavaClass().getName(),
                    instance.getClass().getName());
        }

        if (hasCompositeId()) {
            Map compositeId = java.util.Collections.unmodifiableMap(idProperties.stream()
                    .filter(p -> p.get(instance) != null)
                    .collect(Collectors.toMap(p -> p.getName(), p -> p.get(instance))));
            if (compositeId.isEmpty()) {
                throw new ObjDiffException(ObjDiffExceptionCode.ENTITY_INSTANCE_WITH_NULL_COMPOSITE_ID, getName(), getIdPropertyNames());
            }
            return Collections.unmodifiableMap(compositeId);
        } else {
            Object cdoId = getIdProperty().get(instance);
            if (cdoId == null) {
                throw new ObjDiffException(ObjDiffExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, getName(), getIdProperty().getName());
            }
            return cdoId;
        }
    }

    public InstanceId createIdFromInstance(Object instance) {
        Object localId = getIdOf(instance);
        return instanceIdFactory.create(localId);
    }

    public InstanceId createIdFromInstanceId(Object localId) {
        return instanceIdFactory.create(localId);
    }

    public InstanceId createIdFromDeserializedJsonLocalId(Object localId) {
        return instanceIdFactory.createFromDeserializedJsonLocalId(localId);
    }

    InstanceIdFactory getInstanceIdFactory() {
        return instanceIdFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof EntityType)) {return false;}

        EntityType that = (EntityType) o;
        return super.equals(that) && idProperties.equals(that.idProperties);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + idProperties.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "baseType", getBaseJavaType(),
                "id", getIdPropertyNames());
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addField("idProperties", getIdPropertyNames());
    }

    public Type getLocalIdDehydratedJsonType() {
        return instanceIdFactory.getLocalIdDehydratedJsonType();
    }
}
