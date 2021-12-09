package xyz.arbres.objdiff.core.diff;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.core.Changes;

import java.io.Serializable;
import java.util.List;

/**
 * Diff
 *
 * @author carlos
 * @date 2021-12-07
 */
public class Diff implements Serializable {
    private final Changes changes;
    private final transient PrettyValuePrinter valuePrinter;

    Diff(List<Change> changes, PrettyValuePrinter valuePrinter) {
        this.changes = new Changes(changes, valuePrinter);
        this.valuePrinter = valuePrinter;
    }

    /**
     * Flat list of changes
     *
     */
    public Changes getChanges() {
        return changes;
    }


}
