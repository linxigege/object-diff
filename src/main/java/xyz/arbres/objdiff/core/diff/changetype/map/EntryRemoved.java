package xyz.arbres.objdiff.core.diff.changetype.map;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;

/**
 * Entry removed from a Map
 *
 * @author bartosz walacik
 */
public class EntryRemoved extends EntryAddOrRemove {

    public EntryRemoved(Object key, Object value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return "· entry [" + valuePrinter.formatWithQuotes(getKey()) + " : " +
                valuePrinter.formatWithQuotes(getValue()) + "] removed";
    }
}
