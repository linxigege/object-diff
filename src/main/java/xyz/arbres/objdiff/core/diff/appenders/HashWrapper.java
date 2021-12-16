package xyz.arbres.objdiff.core.diff.appenders;

import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.type.CustomComparableType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * HashWrapper
 *
 * @author carlos
 * @date 2021-12-08
 */
public class HashWrapper {

    private final Object target;
    private final BiFunction<Object, Object, Boolean> equalsFunction;
    private final Function<Object, String> toStringFunction;

    public HashWrapper(Object target, BiFunction<Object, Object, Boolean> equalsFunction, Function<Object, String> toStringFunction) {
        Validate.argumentIsNotNull(equalsFunction);
        Validate.argumentIsNotNull(toStringFunction);
        this.target = target;
        this.equalsFunction = equalsFunction;
        this.toStringFunction = toStringFunction;
    }

    public static Set wrapValuesIfNeeded(Set set, ObjDiffType itemType) {
        if (hasCustomValueComparator(itemType)) {
            CustomComparableType customType = (CustomComparableType) itemType;
            return (Set) set.stream()
                    .map(it -> new HashWrapper(it, itemType::equals, customType::valueToString))
                    .collect(Collectors.toSet());
        }
        return set;
    }

    public static Map wrapKeysIfNeeded(Map map, ObjDiffType keyType) {
        if (hasCustomValueComparator(keyType)) {
            CustomComparableType customType = (CustomComparableType) keyType;
            return (Map) map.entrySet().stream().collect(Collectors.toMap(
                    e -> new HashWrapper(((Map.Entry) e).getKey(), keyType::equals, customType::valueToString),
                    e -> ((Map.Entry) e).getValue()));
        }
        return map;
    }

    private static boolean hasCustomValueComparator(ObjDiffType ObjDiffType) {
        return (ObjDiffType instanceof CustomComparableType &&
                ((CustomComparableType) ObjDiffType).hasCustomValueComparator());
    }

    @Override
    public boolean equals(Object that) {
        return equalsFunction.apply(target, ((HashWrapper) that).target);
    }

    @Override
    public int hashCode() {
        return toStringFunction.apply(target).hashCode();
    }

    public Object unwrap() {
        return target;
    }
}
