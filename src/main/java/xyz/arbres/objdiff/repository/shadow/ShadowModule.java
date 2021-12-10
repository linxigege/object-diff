package xyz.arbres.objdiff.repository.shadow;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class ShadowModule extends InstantiatingModule {
    public ShadowModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                ShadowFactory.class
        );
    }
}
