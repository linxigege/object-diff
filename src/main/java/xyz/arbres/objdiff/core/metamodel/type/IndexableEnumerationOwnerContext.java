package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.core.metamodel.object.EnumerationAwareOwnerContext;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
class IndexableEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private int index;

    IndexableEnumerationOwnerContext(OwnerContext ownerContext) {
        super(ownerContext);
    }

    @Override
    public String getEnumeratorContextPath() {
        return "" + (index++);
    }
}
