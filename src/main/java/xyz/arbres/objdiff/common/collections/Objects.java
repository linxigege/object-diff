package xyz.arbres.objdiff.common.collections;

import xyz.arbres.objdiff.common.validation.Validate;

import java.util.function.BiPredicate;

/**
 * Objects
 *
 * @author carlos
 * @date 2021-12-07
 */
public class Objects {

    private Objects() {
    }

    private static class NullSafetyEqualsWrapper implements BiPredicate<Object, Object> {

        private final BiPredicate<Object, Object> delegate;

        private NullSafetyEqualsWrapper(BiPredicate<Object, Object> delegate) {
            Validate.argumentIsNotNull(delegate);
            this.delegate = delegate;
        }

        @Override
        public boolean test(Object o1, Object o2) {
            if (o1 == null && o2 == null) {
                return true;
            }
            if (o1 == null || o2 == null) {
                return false;
            }
            return delegate.test(o1, o2);
        }
    }

    public static BiPredicate<Object, Object> nullSafetyWrapper(BiPredicate<Object, Object> unsafeEquals) {
        return new NullSafetyEqualsWrapper(unsafeEquals);
    }
}
