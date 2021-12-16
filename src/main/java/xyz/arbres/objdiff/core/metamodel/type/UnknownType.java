package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.ObjDiffBuilder;

import java.util.Optional;

/**
 * Generic type created when a class definition for named type is missing.
 * <br/><br/>
 * Should be avoided because Snapshots with UnknownType can't be properly deserialized,
 * see {@link ObjDiffBuilder#withPackagesToScan(String)}.
 */
public class UnknownType extends ManagedType {

    public UnknownType(String typeName) {
        super(ManagedClass.unknown(), Optional.of(typeName));

    }

    @Override
    ManagedType spawn(ManagedClass managedClass, Optional<String> typeName) {
        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
    }

}
