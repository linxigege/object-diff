package xyz.arbres.objdiff.core;

/**
 * A strategy used in
 * {@link ObjDiffBuilder#registerIgnoredClassesStrategy(IgnoredClassesStrategy)}
 */
@FunctionalInterface
public interface IgnoredClassesStrategy {

    /**
     * Allows to mark classes as ignored by ObjDiff.
     * <br/><br/>
     * <p>
     * When a class is ignored, all properties
     * (found in other classes) with this class type are ignored.
     * <br/><br/>
     * <p>
     * Called in runtime once for each class
     *
     * @return true if a class should be ignored
     */
    boolean isIgnored(Class<?> domainClass);
}
