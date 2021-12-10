package xyz.arbres.objdiff.core;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.json.JsonConverterBuilder;
import xyz.arbres.objdiff.core.metamodel.object.GlobalIdFactory;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CoreObjDiffModule extends InstantiatingModule {
    public CoreObjDiffModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return Lists.<Class>asList(
                ObjDiffCore.class,
                JsonConverterBuilder.class,
                GlobalIdFactory.class
        );
    }
}