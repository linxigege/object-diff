package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.InstanceId;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class InstanceIdFactory {
    private final EntityType entityType;

    InstanceIdFactory(EntityType entityType) {
        this.entityType = entityType;
    }

    InstanceId create(Object localId) {
        Validate.argumentsAreNotNull(entityType, localId);

        DehydratedLocalId dehydratedLocalId = dehydratedLocalId(localId);

        return new InstanceId(entityType.getName(),
                dehydratedLocalId.getId(),
                dehydratedLocalId.toLocalIdString());
    }

    InstanceId createFromDeserializedJsonLocalId(Object deserializedLocalId) {
        Validate.argumentsAreNotNull(entityType, deserializedLocalId);

        String localIdAsString = localIdAsStringFromJson(deserializedLocalId);

        return new InstanceId(entityType.getName(), deserializedLocalId, localIdAsString);
    }

    private String localIdAsStringFromJson(Object deserializedJsonLocalId) {
        if (deserializedJsonLocalId instanceof String) {
            return (String) deserializedJsonLocalId;
        }

        ObjDiffProperty idProperty = entityType.getIdProperty();
        if (idProperty.isEntityType()) {
            EntityType idPropertyType = idProperty.getType();
            return idPropertyType.getInstanceIdFactory().localIdAsStringFromJson(deserializedJsonLocalId);
        }
        if (idProperty.isValueObjectType()) {
            ValueObjectType valueObjectType = idProperty.getType();
            return valueObjectType.smartToString(deserializedJsonLocalId);
        }
        if (idProperty.isPrimitiveOrValueType()) {
            PrimitiveOrValueType primitiveOrValueType = idProperty.getType();
            return primitiveOrValueType.valueToString(deserializedJsonLocalId);
        }

        throw idTypeNotSupported();
    }

    private DehydratedLocalId dehydratedLocalId(ObjDiffProperty idProperty, Object localId) {
        if (idProperty.isEntityType()) {
            EntityType idPropertyType = idProperty.getType();
            return idPropertyType.getInstanceIdFactory().dehydratedLocalId(idPropertyType.getIdOf(localId));
        }
        if (idProperty.isValueObjectType()) {
            ValueObjectType valueObjectType = idProperty.getType();
            String localIdAsString = valueObjectType.smartToString(localId);
            return new SimpleDehydratedLocalId(localIdAsString, valueObjectType.smartToString(localId));
        }
        if (idProperty.isPrimitiveOrValueType()) {
            PrimitiveOrValueType primitiveOrValueType = idProperty.getType();
            return new SimpleDehydratedLocalId(localId, primitiveOrValueType.valueToString(localId));
        }

        throw idTypeNotSupported();
    }


    private DehydratedLocalId dehydratedLocalId(Object localId) {

        if (entityType.hasCompositeId()) {
            Map<String, ?> compositeLocalId = (Map) localId;

            return new CompositeDehydratedLocalId(compositeLocalId
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> dehydratedLocalId(entityType.getProperty(e.getKey()), e.getValue()))
                    .collect(Collectors.toList()));
        }

        return dehydratedLocalId(entityType.getIdProperty(), localId);
    }

    private ObjDiffException idTypeNotSupported() {
        return new ObjDiffException(ObjDiffExceptionCode.ID_TYPE_NOT_SUPPORTED,
                entityType.getIdProperty().getType().getClass().getSimpleName(),
                entityType.getIdProperty().getType().getName(),
                entityType.getBaseJavaClass().getName());
    }

    Type getLocalIdDehydratedJsonType() {
        if (entityType.hasCompositeId()) {
            return String.class;
        }

        ObjDiffProperty idProperty = entityType.getIdProperty();

        if (idProperty.isEntityType()) {
            EntityType idPropertyType = idProperty.getType();
            return idPropertyType.getLocalIdDehydratedJsonType();
        }
        if (idProperty.isValueObjectType()) {
            return String.class;
        }
        if (idProperty.isPrimitiveOrValueType()) {
            return idProperty.getGenericType();
        }

        throw idTypeNotSupported();
    }

    private interface DehydratedLocalId {
        String toLocalIdString();

        Object getId();
    }

    private static class SimpleDehydratedLocalId implements DehydratedLocalId {
        private final Object localId;
        private final String localIdAsString;

        private SimpleDehydratedLocalId(Object localId, String localIdAsString) {
            this.localId = localId;
            this.localIdAsString = localIdAsString;
        }

        @Override
        public String toLocalIdString() {
            return localIdAsString;
        }

        @Override
        public Object getId() {
            return localId;
        }
    }

    private static class CompositeDehydratedLocalId implements DehydratedLocalId {
        private final List<DehydratedLocalId> dehydratedLocalIds;

        private CompositeDehydratedLocalId(List<DehydratedLocalId> dehydratedLocalIds) {
            this.dehydratedLocalIds = dehydratedLocalIds;
        }

        @Override
        public Object getId() {
            return toLocalIdString();
        }

        @Override
        public String toLocalIdString() {
            return String.join(",", dehydratedLocalIds.stream().map(it -> it.toLocalIdString()).collect(Collectors.toList()));
        }
    }
}