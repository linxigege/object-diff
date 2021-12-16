package xyz.arbres.objdiff.core.graph;

import java.util.Optional;

/**
 * Object access hook
 * <br><br>
 * <p>
 * Used for accessing object before commit.
 * i.e. to unproxy hibernate object before comparison
 * <p>
 * Needs to be idempotent because ObjDiff could call it more than once during diff.
 */
public interface ObjectAccessHook<T> {
    /**
     * Return object wrapper, possibly without proxy initialization
     */
    Optional<ObjectAccessProxy<T>> createAccessor(final T entity);
}
