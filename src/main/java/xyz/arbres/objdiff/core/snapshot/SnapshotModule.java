package xyz.arbres.objdiff.core.snapshot;



import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class SnapshotModule extends InstantiatingModule {
    public SnapshotModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                SnapshotFactory.class,
                SnapshotDiffer.class,
                SnapshotGraphFactory.class,
                ChangedCdoSnapshotsFactory.class
        );
    }
}
