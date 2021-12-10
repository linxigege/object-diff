package xyz.arbres.objdiff.core.commit;

import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CommitFactoryModule extends InstantiatingModule {
    public CommitFactoryModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                CommitFactory.class,
                CommitSeqGenerator.class,
                CommitIdFactory.class,
                DistributedCommitSeqGenerator.class
        );
    }
}
