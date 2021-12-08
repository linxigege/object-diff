package xyz.arbres.objdiff.core.diff.changetype.map;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;

/**
 * Entry added to a Map
 *
 * @author bartosz walacik
 */
public class EntryAdded extends EntryAddOrRemove {

    public EntryAdded(Object key, Object value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return "· entry ["+ valuePrinter.formatWithQuotes(getKey()) + " : " +
               valuePrinter.formatWithQuotes(getValue()) + "] added";
    }
}
