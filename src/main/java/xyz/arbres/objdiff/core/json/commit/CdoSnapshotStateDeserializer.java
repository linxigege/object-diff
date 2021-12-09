package xyz.arbres.objdiff.core.json.commit;

import com.google.gson.*;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshotState;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshotStateBuilder;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.*;

import java.lang.reflect.Type;
import java.time.format.DateTimeParseException;
import java.util.Optional;


/**
 * CdoSnapshotState can't be created by standard
 * due to required managedType
 *
 * @author bartosz walacik
 */
class CdoSnapshotStateDeserializer {

    private final TypeMapper typeMapper;
    private final JsonDeserializationContext context;


    public CdoSnapshotStateDeserializer(TypeMapper typeMapper, JsonDeserializationContext context) {
        this.typeMapper = typeMapper;
        this.context = context;
    }

    public CdoSnapshotState deserialize(JsonElement stateElement, ManagedType managedType){
        Validate.argumentsAreNotNull(stateElement, managedType, context);
        JsonObject stateObject = (JsonObject) stateElement;

        CdoSnapshotStateBuilder builder = CdoSnapshotStateBuilder.cdoSnapshotState();

        stateObject.entrySet().stream().forEach(e -> {
            builder.withPropertyValue(e.getKey(),
                    decodePropertyValue(e.getValue(), context, managedType.findProperty(e.getKey())));

        });

        return builder.build();
    }

    private Object decodePropertyValue(JsonElement propertyElement, JsonDeserializationContext context, Optional<ObjDiffProperty> ObjDiffPropertyOptional) {

        if (!ObjDiffPropertyOptional.isPresent()) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        ObjDiffProperty ObjDiffProperty = ObjDiffPropertyOptional.get();
        ObjDiffType expectedObjDiffType = ObjDiffProperty.getType();

        // if primitives on both sides, they should match, otherwise, expectedType is ignored
        if (unmatchedPrimitivesOnBothSides(expectedObjDiffType, propertyElement)) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        // if collections of primitives on both sides, item types should match,
        // otherwise, item type from expectedType is ignored
        if (shouldUseBareContainerClass(expectedObjDiffType, propertyElement)) {
            return context.deserialize(propertyElement, ((ContainerType) expectedObjDiffType).getBaseJavaClass());
        }

        try {
            Type expectedJavaType = typeMapper.getDehydratedType(ObjDiffProperty.getGenericType());
            if (ObjDiffProperty.getType() instanceof TokenType) {
                return deserializeValueWithTypeGuessing(propertyElement, context);
            } else {
                return context.deserialize(propertyElement, expectedJavaType);
            }
        } catch (JsonSyntaxException | DateTimeParseException e) {

            // when users's class is refactored, persisted property value
            // can have different type than expected
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }
    }

    private Object deserializeValueWithTypeGuessing(JsonElement propertyElement, JsonDeserializationContext context) {
        if (propertyElement.isJsonPrimitive()){
            JsonPrimitive jsonPrimitive = (JsonPrimitive) propertyElement;

            if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            }
            if (jsonPrimitive.isNumber()) {
                if (jsonPrimitive.getAsString().equals(jsonPrimitive.getAsInt()+"")) {
                    return jsonPrimitive.getAsInt();
                }
                if (jsonPrimitive.getAsString().equals(jsonPrimitive.getAsLong()+"")) {
                    return jsonPrimitive.getAsLong();
                }
            }
        }
        return context.deserialize(propertyElement, Object.class);
    }

    private boolean unmatchedPrimitivesOnBothSides(ObjDiffType expectedObjDiffType, JsonElement propertyElement) {
        if (ifPrimitivesOnBothSides(expectedObjDiffType, propertyElement)) {
            return !matches((PrimitiveOrValueType)expectedObjDiffType, (JsonPrimitive) propertyElement);
        }
        return false;
    }

    private boolean ifPrimitivesOnBothSides(ObjDiffType expectedObjDiffType, JsonElement propertyElement) {
        return expectedObjDiffType instanceof PrimitiveOrValueType &&
                ((PrimitiveOrValueType) expectedObjDiffType).isJsonPrimitive() &&
                propertyElement instanceof JsonPrimitive;
    }

    private boolean shouldUseBareContainerClass(ObjDiffType expectedObjDiffType, JsonElement propertyElement){
        if(!(expectedObjDiffType instanceof ContainerType) || !(propertyElement instanceof JsonArray)){
            return false;
        }

        ContainerType expectedContainerType = (ContainerType) expectedObjDiffType;
        JsonArray propertyArray = (JsonArray) propertyElement;

        if (propertyArray.size() == 0) {
            return false;
        }

        JsonElement firstItem = propertyArray.get(0);
        ObjDiffType itemType = expectedContainerType.getItemObjDiffType();
        return unmatchedPrimitivesOnBothSides(itemType, firstItem);
    }

    private boolean matches(PrimitiveOrValueType ObjDiffPrimitive, JsonPrimitive jsonPrimitive) {
        return (jsonPrimitive.isNumber() && ObjDiffPrimitive.isNumber()) ||
               (jsonPrimitive.isString() && ObjDiffPrimitive.isStringy()) ||
               (jsonPrimitive.isBoolean() && ObjDiffPrimitive.isBoolean());

    }

    private Object decodePropertyValueUsingJsonType(JsonElement propertyElement, JsonDeserializationContext context) {
        if (GlobalIdTypeAdapter.looksLikeGlobalId(propertyElement)) {
            return context.deserialize(propertyElement, GlobalId.class);
        }
        return context.deserialize(propertyElement, Object.class);
    }
}