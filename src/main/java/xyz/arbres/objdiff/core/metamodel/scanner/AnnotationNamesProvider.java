package xyz.arbres.objdiff.core.metamodel.scanner;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author bartosz walacik
 */
class AnnotationNamesProvider {
    private final Set<String> entityAliases = new HashSet<>();
    private final Set<String> typeNameAliases = new HashSet<>();
    private final Set<String> valueObjectAliases = new HashSet<>();
    private final Set<String> valueAliases = new HashSet<>();
    private final Set<String> transientPropertyAliases = new HashSet<>();
    private final Set<String> propertyNameAliases = new HashSet<>();

    private final List<AnnotationsNameSpace> namesProviders = Lists.immutableListOf(
            new JPAAnnotationsNameSpace());


    AnnotationNamesProvider() {
        for (AnnotationsNameSpace provider : namesProviders) {
            entityAliases.addAll(provider.getEntityAliases());
            valueObjectAliases.addAll(provider.getValueObjectAliases());
            valueAliases.addAll(provider.getValueAliases());
            transientPropertyAliases.addAll(provider.getTransientPropertyAliases());
            typeNameAliases.addAll(provider.getTypeNameAliases());
            propertyNameAliases.addAll(provider.getPropertyNameAliases());
        }
    }

    boolean hasEntityAnnAlias(Set<Class<? extends Annotation>> annTypes) {
        return annTypes.stream().anyMatch(annType -> entityAliases.contains(annType.getSimpleName()));
    }

    boolean hasValueObjectAnnAlias(Set<Class<? extends Annotation>> annTypes) {
        return annTypes.stream().anyMatch(annType -> valueObjectAliases.contains(annType.getSimpleName()));
    }

    boolean hasValueAnnAlias(Set<Class<? extends Annotation>> annTypes) {
        return annTypes.stream().anyMatch(annType -> valueAliases.contains(annType.getSimpleName()));
    }

    boolean hasTransientPropertyAnn(Set<Class<? extends Annotation>> annTypes) {
        return annTypes.contains(ObjDiffAnnotationsNameSpace.DIFF_IGNORE_ANN) ||
                annTypes.stream().anyMatch(annType -> transientPropertyAliases.contains(annType.getSimpleName()));
    }

    boolean hasDiffIncludeAnn(Set<Class<? extends Annotation>> annTypes) {
        return annTypes.contains(ObjDiffAnnotationsNameSpace.DIFF_INCLUDE_ANN);
    }

    boolean hasShallowReferenceAnn(Set<Class<? extends Annotation>> annTypes) {
        return annTypes.contains(ObjDiffAnnotationsNameSpace.SHALLOW_REFERENCE_ANN);
    }

    Optional<String> findTypeNameAnnValue(Set<Annotation> annotations) {
        return getAnnotationValue(annotations, ObjDiffAnnotationsNameSpace.TYPE_NAME_ANN, typeNameAliases);
    }

    Optional<String> findPropertyNameAnnValue(Set<Annotation> annotations) {
        return getAnnotationValue(annotations, ObjDiffAnnotationsNameSpace.PROPERTY_NAME_ANN, propertyNameAliases);
    }

    private Optional<String> getAnnotationValue(Set<Annotation> annotations, Class<? extends Annotation> ObjDiffAnnType, Set<String> aliases) {
        Optional<Annotation> annotation = findAnnotation(annotations, ObjDiffAnnType, aliases);
        return annotation.map(ann -> ReflectionUtil.getAnnotationValue(ann, "value"));
    }

    private Optional<Annotation> findAnnotation(Set<Annotation> annotations, Class<? extends Annotation> ObjDiffAnnType, Set<String> aliases) {
        Optional<Annotation> jTypeName = annotations.stream()
                .filter(ann -> ObjDiffAnnType.isAssignableFrom(ann.getClass()))
                .findAny();
        if (jTypeName.isPresent()) {
            return jTypeName;
        }

        return annotations.stream()
                .filter(ann -> aliases.contains(ann.annotationType().getSimpleName()))
                .findFirst();
    }
}
