package xyz.arbres.objdiff.core.diff;



import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.pico.ObjDiffModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class DiffFactoryModule implements ObjDiffModule {

    @Override
    public Collection<Class> getComponents() {
        return (Collection) Lists.asList(
                DiffFactory.class
        );
    }
}
