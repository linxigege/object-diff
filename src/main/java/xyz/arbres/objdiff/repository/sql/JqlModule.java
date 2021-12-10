package xyz.arbres.objdiff.repository.sql;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

public class JqlModule extends InstantiatingModule {
    public JqlModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return Lists.<Class>asList(

        );
    }

}
