package xyz.arbres.objdiff.common.validation;

/**
 * Validate
 *
 * @author carlos
 * @date 2021-12-07
 */
public class Validate {
    /**
     * Checks if given argument is not null.
     *
     * @throws IllegalArgumentException
     */
    public static void argumentIsNotNull(Object argument, String message) {
        if (argument == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @throws IllegalArgumentException
     */
    public static void argumentIsNotNull(Object argument) {
        argumentIsNotNull(argument, "argument should not be null");
    }

    /**
     * @throws IllegalArgumentException
     */
    public static void argumentsAreNotNull(Object... arguments) {
        for (Object argument : arguments) {
            if (argument == null) {
                throw new IllegalArgumentException("argument should not be null");
            }
        }
    }

    /**
     * @throws IllegalArgumentException
     */
    public static void argumentCheck(boolean argumentCondition, String message) {
        if (!argumentCondition) {
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * @throws IllegalStateException
     */
    public static void conditionFulfilled(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
