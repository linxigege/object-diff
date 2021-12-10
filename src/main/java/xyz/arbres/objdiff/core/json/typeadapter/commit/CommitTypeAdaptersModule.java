package xyz.arbres.objdiff.core.json.typeadapter.commit;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CommitTypeAdaptersModule extends InstantiatingModule {
    public CommitTypeAdaptersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                CdoSnapshotTypeAdapter.class,
                GlobalIdTypeAdapter.class,
                CommitIdTypeAdapter.class,
                JsonElementFakeAdapter.class,
                CdoSnapshotStateTypeAdapter.class,
                CommitMetadataTypeAdapter.class
        );
    }
}
