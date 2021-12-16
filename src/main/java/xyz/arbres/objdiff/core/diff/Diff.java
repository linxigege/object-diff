package xyz.arbres.objdiff.core.diff;

import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.core.Changes;
import xyz.arbres.objdiff.core.ChangesByObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     */
    public Changes getChanges() {
        return changes;
    }

    public String changesSummary() {
        StringBuilder b = new StringBuilder();

        b.append("changes - ");
        for (Map.Entry<Class<? extends Change>, Integer> e : countByType().entrySet()) {
            b.append(e.getKey().getSimpleName() + ":" + e.getValue() + " ");
        }
        return b.toString().trim();
    }

    public Map<Class<? extends Change>, Integer> countByType() {
        Map<Class<? extends Change>, Integer> result = new HashMap<>();
        for (Change change : changes) {
            Class<? extends Change> key = change.getClass();
            if (result.containsKey(change.getClass())) {
                result.put(key, (result.get(key)) + 1);
            } else {
                result.put(key, 1);
            }
        }
        return result;
    }

    public final String prettyPrint() {
        return toString();
    }

    public List<ChangesByObject> groupByObject() {
        return new Changes(changes, valuePrinter).groupByObject();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("Diff:\n");

        groupByObject().forEach(it -> b.append(it.toString()));

        return b.toString();
    }
}
