package xyz.arbres.objdiff.core.diff.changetype.map;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.core.diff.Atomic;

import java.util.Objects;

/**
 * Changed value assigned to a key in a Map
 *
 * @author bartosz walacik
 */
public class EntryValueChange extends EntryChange {
    private final Atomic leftValue;
    private final Atomic rightValue;

    public EntryValueChange(Object key, Object leftValue, Object rightValue) {
        super(key);
        this.leftValue = new Atomic(leftValue);
        this.rightValue = new Atomic(rightValue);
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

    public Atomic getWrappedLeftValue() {
        return leftValue;
    }

    public Atomic getWrappedRightValue() {
        return rightValue;
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return "· entry ["+ valuePrinter.formatWithQuotes(getKey()) + " : " +
                valuePrinter.formatWithQuotes(getLeftValue())+"] -> ["+
                valuePrinter.formatWithQuotes(getKey()) + " : " +
                valuePrinter.formatWithQuotes(getRightValue())+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof EntryValueChange) {
            EntryValueChange that = (EntryValueChange) obj;
            return super.equals(that)
                    && Objects.equals(this.leftValue, that.leftValue)
                    && Objects.equals(this.rightValue, that.rightValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), leftValue, rightValue);
    }
}
