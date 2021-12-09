package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;

class LiveCdoWrapper extends LiveCdo {
    private Object wrappedCdo;

    LiveCdoWrapper(Object wrappedCdo, GlobalId globalId, ManagedType managedType) {
        super(globalId, managedType);

        this.wrappedCdo = wrappedCdo;
    }

    @Override
    Object wrappedCdo() {
        return wrappedCdo;
    }
}
