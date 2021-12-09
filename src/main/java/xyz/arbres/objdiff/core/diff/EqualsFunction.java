package xyz.arbres.objdiff.core.diff;

/**
 * @author bartosz.walacik
 */
public interface EqualsFunction {
    boolean nullSafeEquals(Object left, Object right);
}
