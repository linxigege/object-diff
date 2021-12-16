package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.metamodel.clazz.PropertiesFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ManagedPropertiesFilter
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ManagedPropertiesFilter {

    private final Set<ObjDiffProperty> includedProperties;
    private final Set<ObjDiffProperty> ignoredProperties;

    ManagedPropertiesFilter(Class<?> baseJavaClass, List<ObjDiffProperty> allSourceProperties, PropertiesFilter propertiesFilter) {
        this.includedProperties = filter(allSourceProperties, propertiesFilter.getIncludedProperties(), baseJavaClass);
        this.includedProperties.addAll(allSourceProperties.stream().filter(p -> p.isHasIncludedAnn()).collect(Collectors.toSet()));

        if (this.includedProperties.size() > 0) {
            this.ignoredProperties = Collections.emptySet();
        } else {
            this.ignoredProperties = filter(allSourceProperties, propertiesFilter.getIgnoredProperties(), baseJavaClass);
            this.ignoredProperties.addAll(allSourceProperties.stream().filter(p -> p.hasTransientAnn()).collect(Collectors.toSet()));
        }
    }

    private ManagedPropertiesFilter() {
        this.includedProperties = Collections.emptySet();
        this.ignoredProperties = Collections.emptySet();
    }

    static ManagedPropertiesFilter empty() {
        return new ManagedPropertiesFilter();
    }

    List<ObjDiffProperty> filterProperties(List<ObjDiffProperty> allProperties) {
        if (hasIncludedProperties()) {
            return new ArrayList<>(includedProperties);
        }

        if (hasIgnoredProperties()) {
            return allProperties.stream().filter(it -> !ignoredProperties.contains(it)).collect(Collectors.toList());
        }

        return allProperties;
    }

    boolean hasIgnoredProperties() {
        return !ignoredProperties.isEmpty();
    }

    boolean hasIncludedProperties() {
        return !includedProperties.isEmpty();
    }

    private Set<ObjDiffProperty> filter(List<ObjDiffProperty> allProperties, List<String> propertyNames, Class<?> baseJavaClass) {
        return propertyNames.stream()
                .map(p -> allProperties.stream()
                        .filter(jp -> jp.getName().equals(p))
                        .findFirst()
                        .orElseThrow(() -> new ObjDiffException(ObjDiffExceptionCode.PROPERTY_NOT_FOUND, p, baseJavaClass.getName())))
                .collect(Collectors.toSet());
    }
}
