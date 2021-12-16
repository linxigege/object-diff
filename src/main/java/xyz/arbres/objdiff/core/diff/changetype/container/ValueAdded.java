package xyz.arbres.objdiff.core.diff.changetype.container;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;

/**
 * Item added to an Array or Collection
 *
 * @author bartosz walacik
 */
public class ValueAdded extends ValueAddOrRemove {

    public ValueAdded(int index, Object value) {
        super(index, value);
    }

    public ValueAdded(Object value) {
        super(value);
    }

    /**
     * Added item. See {@link #getValue()} javadoc
     */
    public Object getAddedValue() {
        return value.unwrap();
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return (getIndex() == null ? "· " : getIndex() + ". ") +
                valuePrinter.formatWithQuotes(getAddedValue()) + " added";
    }
}
