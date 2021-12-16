package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.util.Objects;
import java.util.Optional;

/**
 * ReferenceChange
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ReferenceChange extends PropertyChange {
    private final GlobalId left;
    private final GlobalId right;
    private transient final Optional<Object> leftObject;
    private transient final Optional<Object> rightObject;

    public ReferenceChange(PropertyChangeMetadata metadata,
                           GlobalId leftReference, GlobalId rightReference,
                           Object leftObject, Object rightObject) {
        super(metadata);
        this.left = leftReference;
        this.right = rightReference;
        this.leftObject = Optional.ofNullable(leftObject);
        this.rightObject = Optional.ofNullable(rightObject);
    }

    /**
     * GlobalId of left (or previous) domain object reference.
     */
    @Override
    public GlobalId getLeft() {
        return left;
    }

    /**
     * GlobalId of right (or current) domain object reference
     */
    @Override
    public GlobalId getRight() {
        return right;
    }

    /**
     * Domain object reference at left side of a diff.
     * <br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from ObjDiffRepository
     */
    public Optional<Object> getLeftObject() {
        return leftObject;
    }

    /**
     * Domain object reference at right side of a diff.
     * <br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from ObjDiffRepository
     */
    public Optional<Object> getRightObject() {
        return rightObject;
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        if (isPropertyAdded()) {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                    " property with reference " + valuePrinter.formatWithQuotes(getRight()) + " added";
        } else if (isPropertyRemoved()) {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                    " property with reference " + valuePrinter.formatWithQuotes(getLeft()) + " removed";
        } else {
            if (left == null) {
                return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                        " = " + valuePrinter.formatWithQuotes(getRight());
            } else if (right == null) {
                return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                        " reference " + valuePrinter.formatWithQuotes(getLeft()) + " unset";
            } else {
                return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                        " reference changed: " + valuePrinter.formatWithQuotes(getLeft()) + " -> " +
                        valuePrinter.formatWithQuotes(getRight());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReferenceChange) {
            ReferenceChange that = (ReferenceChange) obj;
            return super.equals(that)
                    && Objects.equals(this.getLeft(), that.getLeft())
                    && Objects.equals(this.getRight(), that.getRight());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeft(), getRight());
    }

    @Override
    public String toString() {
        PrettyValuePrinter valuePrinter = PrettyValuePrinter.getDefault();
        return this.getClass().getSimpleName() + "{ property: '" + getPropertyName() + "'," +
                " left:" + valuePrinter.formatWithQuotes(getLeft()) + ", " +
                " right:" + valuePrinter.formatWithQuotes(getRight()) + " }";
    }
}
