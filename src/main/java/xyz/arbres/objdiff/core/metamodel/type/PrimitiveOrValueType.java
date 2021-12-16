package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.Primitives;
import xyz.arbres.objdiff.common.string.PrettyPrintBuilder;
import xyz.arbres.objdiff.core.diff.customer.CustomValueComparator;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public abstract class PrimitiveOrValueType<T> extends ClassType implements CustomComparableType {
    private final CustomValueComparator<T> valueComparator;

    PrimitiveOrValueType(Type baseJavaType) {
        this(baseJavaType, null);
    }

    PrimitiveOrValueType(Type baseJavaType, CustomValueComparator<T> comparator) {
        super(baseJavaType);
        this.valueComparator = (comparator == null || comparator.handlesNulls()) ?
                comparator :
                new CustomValueComparatorNullSafe<>(comparator);
    }

    @Override
    public boolean hasCustomValueComparator() {
        return valueComparator != null;
    }

    @Override
    public boolean equals(Object left, Object right) {
        if (valueComparator != null) {
            return valueComparator.equals((T) left, (T) right);
        }
        return super.equals(left, right);
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(getBaseJavaClass()) ||
                Primitives.isPrimitiveNumber(getBaseJavaClass());
    }

    public boolean isBoolean() {
        return Boolean.class == getBaseJavaClass() || boolean.class == getBaseJavaClass();
    }

    public boolean isStringy() {
        return String.class == getBaseJavaClass() ||
                CharSequence.class == getBaseJavaClass() ||
                char.class == getBaseJavaClass() ||
                Character.class == getBaseJavaClass();
    }

    public boolean isJsonPrimitive() {
        return isStringy() || isBoolean() || isNumber();
    }

    CustomValueComparator getValueComparator() {
        return valueComparator;
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addField("valueComparator", valueComparator);
    }
}
