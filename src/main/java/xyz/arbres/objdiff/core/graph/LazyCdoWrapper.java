package xyz.arbres.objdiff.core.graph;



import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.ManagedType;

import java.util.function.Supplier;

class LazyCdoWrapper extends LiveCdo {
    private final Supplier<?> cdoSupplier;

    LazyCdoWrapper(Supplier<?> cdoSupplier, GlobalId globalId, ManagedType managedType) {
        super(globalId, managedType);
        this.cdoSupplier = cdoSupplier;
    }

    @Override
    Object wrappedCdo() {
        return cdoSupplier.get();
    }
}
