package xyz.arbres.objdiff.core.metamodel.clazz;

import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.validation.Validate;

import java.util.List;

/**
 * PropertiesFilter
 *
 * @author carlos
 * @date 2021-12-08
 */
public class PropertiesFilter {
    private final List<String> includedProperties;
    private final List<String> ignoredProperties;

    public PropertiesFilter(List<String> includedProperties, List<String> ignoredProperties) {
        Validate.argumentsAreNotNull(ignoredProperties, includedProperties);
        this.includedProperties = Lists.immutableCopyOf(includedProperties);
        this.ignoredProperties = Lists.immutableCopyOf(ignoredProperties);
    }

    public List<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    public List<String> getIncludedProperties() {
        return includedProperties;
    }
}
