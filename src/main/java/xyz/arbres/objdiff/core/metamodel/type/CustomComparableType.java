package xyz.arbres.objdiff.core.metamodel.type;

public interface CustomComparableType {

    boolean hasCustomValueComparator();

    String valueToString(Object value);
}
