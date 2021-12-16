package xyz.arbres.objdiff.core.diff.changetype;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;

import java.util.Objects;

/**
 * ValueChange
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ValueChange extends PropertyChange<Object> {

    private final Atomic left;
    private final Atomic right;

    public ValueChange(PropertyChangeMetadata metadata, Object leftValue, Object rightValue) {
        super(metadata);
        this.left = new Atomic(leftValue);
        this.right = new Atomic(rightValue);
    }

    @Override
    public Object getLeft() {
        return left.unwrap();
    }

    @Override
    public Object getRight() {
        return right.unwrap();
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        if (isPropertyAdded()) {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                    " property with value " + valuePrinter.formatWithQuotes(right.unwrap()) + " added";
        } else if (isPropertyRemoved()) {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                    " property with value " + valuePrinter.formatWithQuotes(left.unwrap()) + " removed";
        } else {
            if (left.isNull()) {
                return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                        " = " + valuePrinter.formatWithQuotes(getRight());
            } else if (right.isNull()) {
                return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                        " value " + valuePrinter.formatWithQuotes(getLeft()) + " unset";
            } else {
                return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                        " changed: " + valuePrinter.formatWithQuotes(getLeft()) + " -> " +
                        valuePrinter.formatWithQuotes(getRight());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ValueChange) {
            ValueChange that = (ValueChange) obj;
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
        PrettyValuePrinter printer = PrettyValuePrinter.getDefault();
        return this.getClass().getSimpleName() + "{ property: '" + getPropertyName() + "'," +
                " left:" + printer.formatWithQuotes(getLeft()) + ", " +
                " right:" + printer.formatWithQuotes(getRight()) + " }";
    }
}
