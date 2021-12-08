package xyz.arbres.objdiff.common.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static xyz.arbres.objdiff.common.validation.Validate.argumentCheck;

/**
 * Collections
 *
 * @author carlos
 * @date 2021-12-07
 */
public class Collections {
    public static <E> Collection<E> difference(Collection<E> first, Collection<E> second) {
        if (first instanceof List) {
            return Lists.difference((List) first,(List) second);
        } else if (first instanceof Set) {
            return Sets.difference((Set) first,(Set) second);
        } else {
            throw new IllegalArgumentException("At this moment ObjDiff don't support "  + first.getClass().getSimpleName());
        }
    }

    public static Collection wrapNull(Object sourceCollection) {
        if (sourceCollection == null) {
            return java.util.Collections.emptySet();
        }
        else{
            return (Collection)sourceCollection;
        }
    }

    public static int size(Object col) {
        if (col == null) {
            return 0;
        }
        argumentCheck(col instanceof Collection, col + " is not a Collection");

        return ((Collection) col).size();
    }
}
