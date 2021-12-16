package xyz.arbres.objdiff.core.graph;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class GraphFactoryModule extends InstantiatingModule {
    public GraphFactoryModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                LiveCdoFactory.class,
                CollectionsCdoFactory.class,
                LiveGraphFactory.class,
                ObjectHasher.class,
                ObjectGraphBuilder.class,
                ObjectAccessHookDoNothingImpl.class);
    }
}
