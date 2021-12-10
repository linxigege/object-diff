package xyz.arbres.objdiff.core.json.typeadapter.change;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class ChangeTypeAdaptersModule extends InstantiatingModule {

    public ChangeTypeAdaptersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                MapChangeTypeAdapter.class,
                ArrayChangeTypeAdapter.class,
                ListChangeTypeAdapter.class,
                SetChangeTypeAdapter.class,
                NewObjectTypeAdapter.class,
                ValueChangeTypeAdapter.class,
                ObjectRemovedTypeAdapter.class,
                ChangeTypeAdapter.class,
                ReferenceChangeTypeAdapter.class
        );
    }
}
