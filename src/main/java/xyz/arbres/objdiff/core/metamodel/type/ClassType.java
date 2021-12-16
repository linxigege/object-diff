package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.common.validation.Validate;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * ClassType
 *
 * @author carlos
 * @date 2021-12-07
 */
abstract class ClassType extends ObjDiffType {

    private final Class baseJavaClass;

    ClassType(Type baseJavaType) {
        this(baseJavaType, Optional.empty());
    }

    ClassType(Type baseJavaType, Optional<String> name) {
        this(baseJavaType, name, 0);
    }

    ClassType(Type baseJavaType, Optional<String> name, int expectedArgs) {
        super(baseJavaType, name, expectedArgs);
        Validate.argumentIsNotNull(name);
        this.baseJavaClass = ReflectionUtil.extractClass(baseJavaType);
    }

    @Override
    public boolean canBePrototype() {
        return true;
    }

    @Override
    public boolean isInstance(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return baseJavaClass.isAssignableFrom(cdo.getClass());
    }

    /**
     * Type for JSON representation.
     * <p>
     * For Values it's simply baseJavaType.
     * <p>
     * For ManagedTypes (references to Entities and ValueObjects) it's GlobalId
     * because ObjDiff serializes references in the 'dehydrated' form.
     */

    protected Type getRawDehydratedType() {
        return baseJavaClass;
    }

    public Class getBaseJavaClass() {
        return baseJavaClass;
    }
}

