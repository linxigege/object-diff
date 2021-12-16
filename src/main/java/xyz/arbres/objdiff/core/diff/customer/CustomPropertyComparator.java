package xyz.arbres.objdiff.core.diff.customer;


import xyz.arbres.objdiff.core.diff.changetype.PropertyChange;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChangeMetadata;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.type.CustomType;

import java.util.Optional;

/**
 * Property-scope comparator bounded to {@link CustomType}.
 * <br/><br/>
 *
 * <b>
 * Custom Types are not easy to manage, use it as a last resort,<br/>
 * only for corner cases like comparing custom Collection types.</b>
 * <br/><br/>
 * <p>
 * Typically, Custom Types are large structures (like Multimap).<br/>
 * Implementation should calculate diff between two objects of given Custom Type.
 * <br/><br/>
 *
 * <b>Usage</b>:
 * <pre>
 * ObjDiffBuilder.ObjDiff()
 *              .registerCustomType( Multimap.class, new GuavaCustomComparator())
 *              .build()
 * </pre>
 *
 * @param <T> Custom Type
 * @param <C> Concrete type of PropertyChange returned by a comparator
 * @see <a href="https://ObjDiff.org/documentation/diff-configuration/#custom-comparators">https://ObjDiff.org/documentation/diff-configuration/#custom-comparators</a>
 * @see CustomValueComparator
 */
public interface CustomPropertyComparator<T, C extends PropertyChange> extends CustomValueComparator<T> {
    /**
     * Called by ObjDiff to calculate property-to-property diff
     * between two Custom Type objects. Can calculate any of concrete {@link PropertyChange}.
     * <p>
     * <br/><br/>
     * Implementation of <code>compare()</code> should be consistent with
     * {@link #equals(Object, Object)}.
     * When <code>compare()</code> returns <code>Optional.empty()</code>,
     * <code>equals()</code> should return false.
     *
     * @param left     left (or old) value
     * @param right    right (or current) value
     * @param metadata call {@link PropertyChangeMetadata#getAffectedCdoId()} to get
     *                 Id of domain object being compared
     * @param property property being compared
     * @return should return Optional.empty() if compared objects are the same
     */
    Optional<C> compare(T left, T right, PropertyChangeMetadata metadata, Property property);
}
