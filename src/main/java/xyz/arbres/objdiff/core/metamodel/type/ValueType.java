package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.WellKnownValueTypes;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.core.ObjDiffBuilder;
import xyz.arbres.objdiff.core.diff.customer.CustomValueComparator;
import xyz.arbres.objdiff.core.json.JsonTypeAdapter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Value class in a client's domain model is a simple value holder.
 * <br/>
 * <p>
 * ObjDiff doesn't interact with internal properties of Values and treats them similarly to primitives.
 * <br/><br/>
 * <p>
 * Two Values are compared using {@link Object#equals(Object)} so
 * it's highly important to implement it properly by comparing underlying fields.
 * <br/><br/>
 * <p>
 * If you don't control the <code>equals()</code> implementation in a Value class you can still
 * provide a {@link CustomValueComparator}
 * and register it with {@link ObjDiffBuilder#registerValue(Class, CustomValueComparator)}.
 * <br/><br/>
 * <p>
 * It's highly advisable to implement Values as immutable objects,
 * like {@link BigDecimal} or {@link LocalDateTime}.
 * <br/><br/>
 * <p>
 * Values are serialized to JSON using Gson defaults,
 * if it's not what you need, implement {@link JsonTypeAdapter} for custom serialization
 * and register it with {@link ObjDiffBuilder#registerValueTypeAdapter(JsonTypeAdapter)}.
 *
 * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#ValueType">http://ObjDiff.org/documentation/domain-configuration/#ValueType</a>
 * @see <a href="https://ObjDiff.org/documentation/diff-configuration/#custom-comparators">https://ObjDiff.org/documentation/diff-configuration/#custom-comparators</a>
 */
public class ValueType extends PrimitiveOrValueType {

    public ValueType(Type baseJavaType) {
        super(baseJavaType);
    }

    public ValueType(Type baseJavaType, CustomValueComparator customValueComparator) {
        super(baseJavaType, customValueComparator);
    }

    @Override
    public String valueToString(Object value) {
        if (value == null) {
            return "";
        }

        if (hasCustomValueComparator()) {
            return getValueComparator().toString(value);
        }

        if (WellKnownValueTypes.isOldGoodValueType(value)) {
            return value.toString();
        }

        //since java 16 we can't access core java classes with reflection
        if (value.getClass().getPackage().getName().startsWith("java")) {
            return value.toString();
        }

        return ReflectionUtil.reflectiveToString(value);
    }
}
