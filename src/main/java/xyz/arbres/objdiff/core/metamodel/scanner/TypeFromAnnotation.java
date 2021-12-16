package xyz.arbres.objdiff.core.metamodel.scanner;


import xyz.arbres.objdiff.core.metamodel.type.*;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author bartosz.walacik
 */
class TypeFromAnnotation {
    private final Optional<Class<? extends ObjDiffType>> ObjDiffType;

    TypeFromAnnotation(Class<? extends Annotation> ObjDiffTypeAnnotation) {
        if (ObjDiffTypeAnnotation == ObjDiffAnnotationsNameSpace.VALUE_ANN) {
            ObjDiffType = Optional.of(ValueType.class);
        } else if (ObjDiffTypeAnnotation == ObjDiffAnnotationsNameSpace.VALUE_OBJECT_ANN) {
            ObjDiffType = Optional.of(ValueObjectType.class);
        } else if (ObjDiffTypeAnnotation == ObjDiffAnnotationsNameSpace.ENTITY_ANN) {
            ObjDiffType = Optional.of(EntityType.class);
        } else if (ObjDiffTypeAnnotation == ObjDiffAnnotationsNameSpace.DIFF_IGNORE_ANN) {
            ObjDiffType = Optional.of(IgnoredType.class);
        } else if (ObjDiffTypeAnnotation == ObjDiffAnnotationsNameSpace.SHALLOW_REFERENCE_ANN) {
            ObjDiffType = Optional.of(ShallowReferenceType.class);
        } else {
            ObjDiffType = Optional.empty();
        }
    }

    TypeFromAnnotation(boolean hasEntity, boolean hasValueObject, boolean hasValue) {
        if (hasEntity) {
            ObjDiffType = Optional.of(EntityType.class);
        } else if (hasValueObject) {
            ObjDiffType = Optional.of(ValueObjectType.class);
        } else if (hasValue) {
            ObjDiffType = Optional.of(ValueType.class);
        } else {
            ObjDiffType = Optional.empty();
        }
    }

    boolean isValue() {
        return ObjDiffType.map(it -> it == ValueType.class).orElse(false);
    }

    boolean isValueObject() {
        return ObjDiffType.map(it -> it == ValueObjectType.class).orElse(false);
    }

    boolean isEntity() {
        return ObjDiffType.map(it -> it == EntityType.class).orElse(false);
    }

    boolean isIgnored() {
        return ObjDiffType.map(it -> it == IgnoredType.class).orElse(false);
    }

    boolean isShallowReference() {
        return ObjDiffType.map(it -> it == ShallowReferenceType.class).orElse(false);
    }
}
