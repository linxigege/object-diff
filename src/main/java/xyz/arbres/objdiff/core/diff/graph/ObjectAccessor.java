package xyz.arbres.objdiff.core.diff.graph;

import java.util.Optional;

public interface ObjectAccessor<T> {

    Class<T> getTargetClass();

    T access();

    Optional<Object> getLocalId();
}
