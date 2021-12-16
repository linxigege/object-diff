package xyz.arbres.objdiff.core.diff.customer;


import xyz.arbres.objdiff.core.diff.ListCompareAlgorithm;
import xyz.arbres.objdiff.core.metamodel.object.InstanceId;
import xyz.arbres.objdiff.core.metamodel.type.ValueType;

/**
 * A custom comparator for {@link ValueType} classes
 * to be used instead of default {@link Object#equals(Object)}.
 * <br/><br/>
 * <p>
 * Example implementation: {@link CustomBigDecimalComparator}
 * <br/><br/>
 *
 * <b>Usage</b>:
 * <pre>
 * ObjDiffBuilder.ObjDiff()
 *              .registerValue(BigDecimal.class, new CustomBigDecimalComparator(2))
 *              .build()
 * </pre>
 *
 * @param <T> Value Type
 * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#ValueType">http://ObjDiff.org/documentation/domain-configuration/#ValueType</a>
 * @see <a href="https://ObjDiff.org/documentation/diff-configuration/#custom-comparators">https://ObjDiff.org/documentation/diff-configuration/#custom-comparators</a>
 */
public interface CustomValueComparator<T> {
    /**
     * Called by ObjDiff to compare two Values.
     *
     * @param a not null if {@link #handlesNulls()} returns false
     * @param b not null if {@link #handlesNulls()} returns false
     */
    boolean equals(T a, T b);

    /**
     * This method has two roles.
     * First, it is used when Values are compared in hashing contexts.
     * Second, it is used to build Entity Ids from Values.
     * <p>
     * <br/><br/>
     * <h2>Hashcode role</h2>
     * <p>
     * When a Value class has custom <code>toString()</code>, it is used
     * instead of {@link Object#hashCode()} when comparing Values in hashing contexts, so:
     *
     * <ul>
     *     <li>Sets with Values</li>
     *     <li>Lists with Values compared as {@link ListCompareAlgorithm#AS_SET}</li>
     *     <li>Maps with Values as keys</li>
     * </ul>
     * <p>
     * Custom <code>toString()</code> implementation should be aligned with custom {@link #equals(Object, Object)}
     * in the same way like {@link Object#hashCode()} should be aligned with {@link Object#equals(Object)}.
     * <p>
     * <br/><br/>
     * <h2>Entity Id role</h2>
     * <p>
     * Each Value can serve as an Entity Id.<br/>
     * <p>
     * When a Value has custom <code>toString()</code>
     * function, it is used for creating {@link InstanceId} for Entities.
     * If a Value doesn't have a custom <code>toString()</code>
     * , default {@link ReflectionUtil#reflectiveToString(Object)}) is used.
     * <br/><br/>
     * <p>
     * See full example <a href="https://github.com/ObjDiff/ObjDiff/blob/master/ObjDiff-core/src/test/groovy/org/ObjDiff/core/examples/CustomToStringExample.groovy">CustomToStringExample.groovy</a>.
     *
     * @param value not null if {@link #handlesNulls()} returns false
     */
    String toString(T value);

    /**
     * This flag is used to indicate to ObjDiff whether
     * a comparator implementation wants to handle nulls.
     * <br /><br />
     * <p>
     * By default, the flag is <b>false</b> and ObjDiff
     * checks if both values are non-null before calling a comparator.
     * <br/>
     * If any of given values is null &mdash; ObjDiff compares them using the
     * standard Java logic:
     * <ul>
     *     <li>null == null</li>
     *     <li>null != non-null</li>
     * </ul>
     * <p>
     * <br/>
     * <p>
     * If the flag is <b>true</b> &mdash; ObjDiff skips that logic and
     * allows a comparator to handle nulls on its own.
     * In that case, a comparator holds responsibility for null-safety.
     *
     * @see NullAsBlankStringComparator
     */
    default boolean handlesNulls() {
        return false;
    }
}
