package xyz.arbres.objdiff.core.metamodel.scanner;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.util.Collections;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class PropertyScan {
    private final List<Property> properties;
    private final boolean hasId;

    PropertyScan(List<Property> properties) {
        Validate.argumentIsNotNull(properties);
        this.properties = properties;
        hasId = properties.stream().anyMatch(p -> p.looksLikeId());
    }

    Property getFirst() {
        return properties.get(0);
    }

    List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    boolean hasId() {
        return hasId;
    }
}
