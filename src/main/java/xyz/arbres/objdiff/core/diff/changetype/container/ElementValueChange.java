package xyz.arbres.objdiff.core.diff.changetype.container;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.core.diff.changetype.Atomic;

import java.util.Objects;

/**
 * Value changed at certain index in an Array or List
 *
 * @author pawel szymczyk
 */
public class ElementValueChange extends ContainerElementChange {

    private final Atomic leftValue;
    private final Atomic rightValue;

    public ElementValueChange(int index, Object leftValue, Object rightValue) {
        super(index);
        this.leftValue = new Atomic(leftValue);
        this.rightValue = new Atomic(rightValue);
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return getIndex() + ". " +
                valuePrinter.formatWithQuotes(getLeftValue()) + " changed to " +
                valuePrinter.formatWithQuotes(getRightValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ElementValueChange) {
            ElementValueChange that = (ElementValueChange) obj;
            return super.equals(that)
                    && Objects.equals(this.getLeftValue(), that.getLeftValue())
                    && Objects.equals(this.getRightValue(), that.getRightValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeftValue(), getRightValue());
    }

}
