package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.core.diff.NodePair;
import xyz.arbres.objdiff.core.diff.changetype.PropertyChange;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

/**
 * Property-scope comparator,
 * follows Chain-of-responsibility pattern.
 * <br/><br/>
 * <p>
 * Implementation should calculate diff between two property values
 *
 * @author bartosz walacik
 */
public interface PropertyChangeAppender<T extends PropertyChange> {
    int HIGH_PRIORITY = 1;
    int LOW_PRIORITY = 2;

    /**
     * Checks if given property type is supported
     */
    boolean supports(ObjDiffType propertyType);

    T calculateChanges(NodePair pair, ObjDiffProperty property);

    default int priority() {
        return LOW_PRIORITY;
    }
}
