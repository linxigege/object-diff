package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.reflection.ReflectionUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * ValueObjectType
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ValueObjectType extends ManagedType {

    private final boolean defaultType;
    private final Optional<Function<Object, String>> toStringFunction = Optional.empty();

    ValueObjectType(ManagedClass valueObject) {
        super(valueObject);
        this.defaultType = false;
    }

    public ValueObjectType(Class baseJavaClass, List<ObjDiffProperty> allProperties) {
        this(new ManagedClass(baseJavaClass, allProperties, Collections.emptyList(), ManagedPropertiesFilter.empty()));
    }

    ValueObjectType(ManagedClass valueObject, Optional<String> typeName, boolean isDefault) {
        super(valueObject, typeName);
        this.defaultType = isDefault;
    }

    @Override
    ValueObjectType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new ValueObjectType(managedClass, typeName, defaultType);
    }

    @Override
    public boolean canBePrototype() {
        return !defaultType;
    }

    public String smartToString(Object value) {
        if (value == null) {
            return "";
        }

        return toStringFunction
                .map(f -> f.apply(value))
                .orElse(ReflectionUtil.reflectiveToString(value));
    }
}
