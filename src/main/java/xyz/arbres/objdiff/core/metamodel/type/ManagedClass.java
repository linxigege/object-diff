package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * ManagedClass
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ManagedClass {
    private final Class<?> baseJavaClass;
    private final Map<String, ObjDiffProperty> propertiesByName;
    private final List<ObjDiffProperty> managedProperties;
    private final List<ObjDiffProperty> looksLikeId;
    private final ManagedPropertiesFilter managedPropertiesFilter;

    ManagedClass(Class baseJavaClass, List<ObjDiffProperty> managedProperties, List<ObjDiffProperty> looksLikeId, ManagedPropertiesFilter managedPropertiesFilter) {
        Validate.argumentsAreNotNull(baseJavaClass, managedProperties, looksLikeId, managedPropertiesFilter);

        this.baseJavaClass = baseJavaClass;

        this.looksLikeId = Lists.immutableCopyOf(looksLikeId);
        this.managedPropertiesFilter = managedPropertiesFilter;
        this.managedProperties = Lists.immutableCopyOf(managedProperties);

        this.propertiesByName = new HashMap<>();
        managedProperties.forEach(property -> propertiesByName.put(property.getName(),property));
    }

    static ManagedClass unknown() {
        return new ManagedClass(Object.class, Collections.emptyList(), Collections.emptyList(), ManagedPropertiesFilter.empty());
    }

    ManagedClass createShallowReference(){
        return new ManagedClass(baseJavaClass, Collections.emptyList(), getLooksLikeId(), ManagedPropertiesFilter.empty());
    }

    ManagedPropertiesFilter getManagedPropertiesFilter() {
        return managedPropertiesFilter;
    }

    /**
     * Returns all managed properties, unmodifiable list
     */
    List<ObjDiffProperty> getManagedProperties() {
        return managedProperties;
    }

    List<ObjDiffProperty> getLooksLikeId() {
        return looksLikeId;
    }

    Set<String> getPropertyNames(){
        return Collections.unmodifiableSet(propertiesByName.keySet());
    }

    /**
     * returns managed properties subset
     */
    List<ObjDiffProperty> getManagedProperties(Predicate<ObjDiffProperty> query) {
        return Lists.positiveFilter(managedProperties, query);
    }

    /**
     * finds property by name (managed or withTransientAnn)
     *
     * @throws ObjDiffException PROPERTY_NOT_FOUND
     */
    ObjDiffProperty getProperty(String withName) {
        Validate.argumentIsNotNull(withName);
        if (!propertiesByName.containsKey(withName)){
            throw new ObjDiffException(ObjDiffExceptionCode.PROPERTY_NOT_FOUND, withName, baseJavaClass.getName());
        }
        return propertiesByName.get(withName);
    }

    /**
     * @throws ObjDiffException PROPERTY_NOT_FOUND
     */
    List<ObjDiffProperty> getProperties(List<String> withNames) {
        Validate.argumentIsNotNull(withNames);
        return withNames.stream().map(n -> getProperty(n)).collect(Collectors.toList());
    }

    boolean hasProperty(String propertyName) {
        return propertiesByName.containsKey(propertyName);
    }

    void forEachProperty(Consumer<ObjDiffProperty> consumer) {
        managedProperties.forEach(p -> consumer.accept(p));
    }

    Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }
}
