package xyz.arbres.objdiff.repository.shadow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.json.JsonConverter;
import xyz.arbres.objdiff.core.metamodel.object.*;
import xyz.arbres.objdiff.core.metamodel.type.*;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Stateful builder
 *
 * @author bartosz.walacik
 */
class ShadowGraphBuilder {
    private final JsonConverter jsonConverter;
    private final BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver;
    private final TypeMapper typeMapper;
    private final CommitMetadata rootContext;
    private boolean built = false;
    private Map<GlobalId, ShadowBuilder> builtNodes = new HashMap<>();

    ShadowGraphBuilder(JsonConverter jsonConverter, BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver, TypeMapper typeMapper, CommitMetadata rootContext) {
        this.jsonConverter = jsonConverter;
        this.referenceResolver = referenceResolver;
        this.typeMapper = typeMapper;
        this.rootContext = rootContext;
    }

    Object buildDeepShadow(CdoSnapshot cdoSnapshot) {
        Validate.argumentIsNotNull(cdoSnapshot);
        switchToBuilt();

        ShadowBuilder root = assembleShadowStub(cdoSnapshot);

        doWiring();

        return root.getShadow();
    }

    private void doWiring() {
        builtNodes.values().forEach(ShadowBuilder::wire);
    }

    private void switchToBuilt() {
        if (built) {
            throw new IllegalStateException("already built");
        }
        built = true;
    }

    private ShadowBuilder assembleShallowReferenceShadow(InstanceId instanceId, EntityType shallowReferenceType) {
        CdoSnapshotState state = CdoSnapshotStateBuilder.cdoSnapshotState().withPropertyValue(shallowReferenceType.getIdProperty(), instanceId.getCdoId()).build();

        Object shadowStub = jsonConverter.fromJson(toJson(state), shallowReferenceType.getBaseJavaClass());

        ShadowBuilder shadowBuilder = new ShadowBuilder(null, shadowStub);
        builtNodes.put(instanceId, shadowBuilder);

        return shadowBuilder;
    }

    private ShadowBuilder assembleShadowStub(CdoSnapshot cdoSnapshot) {
        ShadowBuilder shadowBuilder = new ShadowBuilder(cdoSnapshot, null);
        builtNodes.put(cdoSnapshot.getGlobalId(), shadowBuilder);

        JsonObject jsonElement = toJson(cdoSnapshot.stateWithAllPrimitives());
        mapCustomPropertyNamesToJavaOrigin(cdoSnapshot.getManagedType(), jsonElement);
        followReferences(shadowBuilder, jsonElement);

        shadowBuilder.withStub(
                deserializeObjectFromJsonElement(cdoSnapshot.getManagedType(), jsonElement));

        return shadowBuilder;
    }

    private Object deserializeObjectFromJsonElement(ManagedType managedType, JsonObject jsonElement) {
        try {
            return jsonConverter.fromJson(jsonElement, managedType.getBaseJavaClass());
        } catch (JsonSyntaxException | DateTimeParseException e) {
            return sanitizedDeserialization(jsonElement, managedType);
        }
    }

    private Object sanitizedDeserialization(JsonObject jsonElement, ManagedType managedType) {
        managedType.getProperties().forEach(p -> {
            try {
                jsonConverter.fromJson(jsonElement.get(p.getName()), p.getRawType());
            } catch (Exception e) {
                jsonElement.remove(p.getName());
            }
        });

        return jsonConverter.fromJson(jsonElement, managedType.getBaseJavaClass());
    }

    private void mapCustomPropertyNamesToJavaOrigin(ManagedType managedType, JsonObject jsonElement) {
        managedType.forEachProperty(ObjDiffProperty -> {
            if (ObjDiffProperty.hasCustomName()) {
                JsonElement value = jsonElement.get(ObjDiffProperty.getName());
                jsonElement.remove(ObjDiffProperty.getName());
                jsonElement.add(ObjDiffProperty.getOriginalName(), value);
            }
        });
    }

    private void followReferences(ShadowBuilder currentNode, JsonObject jsonElement) {
        CdoSnapshot cdoSnapshot = currentNode.getCdoSnapshot();

        cdoSnapshot.getManagedType().forEachProperty(property -> {
            if (cdoSnapshot.isNull(property)) {
                return;
            }

            if (property.getType() instanceof ManagedType) {
                GlobalId refId = (GlobalId) cdoSnapshot.getPropertyValue(property);

                ShadowBuilder target = createOrReuseNodeFromRef(refId, property);
                if (target != null) {
                    currentNode.addReferenceWiring(property, target);
                }

                jsonElement.remove(property.getName());
            }

            if (typeMapper.isContainerOfManagedTypes(property.getType()) ||
                    typeMapper.isKeyValueTypeWithManagedTypes(property.getType())) {
                EnumerableType propertyType = property.getType();

                Object containerWithRefs = cdoSnapshot.getPropertyValue(property);
                if (!propertyType.isEmpty(containerWithRefs)) {
                    currentNode.addEnumerableWiring(property, propertyType
                            .map(containerWithRefs, (value) -> passValueOrCreateNodeFromRef(value, property), true));
                    jsonElement.remove(property.getName());
                }
            }
        });
    }

    private Object passValueOrCreateNodeFromRef(Object value, ObjDiffProperty property) {
        if (value instanceof GlobalId) {
            return createOrReuseNodeFromRef((GlobalId) value, property);
        }
        return value;
    }

    private ShadowBuilder createOrReuseNodeFromRef(GlobalId globalId, ObjDiffProperty property) {
        if (builtNodes.containsKey(globalId)) {
            return builtNodes.get(globalId);
        }

        if (property.isShallowReference()) {
            EntityType shallowReferenceType = property.getType() instanceof EntityType
                    ? property.getType()
                    : (EntityType) typeMapper.getObjDiffManagedType(globalId);

            if (shallowReferenceType.getIdProperty().getType() instanceof ValueObjectType) {
                //TODO don't know how to reconstruct Id in ShallowReference, which happened to be a ValueObject,
                return null;
            } else {
                return assembleShallowReferenceShadow((InstanceId) globalId, shallowReferenceType);
            }
        }

        CdoSnapshot cdoSnapshot = referenceResolver.apply(rootContext, globalId);
        if (cdoSnapshot != null) {
            return assembleShadowStub(cdoSnapshot);
        }
        return null;
    }

    private JsonObject toJson(CdoSnapshotState state) {
        return (JsonObject) jsonConverter.toJsonElement(state);
    }
}
