package xyz.arbres.objdiff.core.graph;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.CoreConfiguration;
import xyz.arbres.objdiff.core.MappingStyle;
import xyz.arbres.objdiff.core.pico.LateInstantiatingModule;

import java.util.Collection;

public class TailoredObjDiffMemberFactoryModule extends LateInstantiatingModule {

    public TailoredObjDiffMemberFactoryModule(CoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        MappingStyle mappingStyle = getConfiguration().getMappingStyle();

        if (mappingStyle == MappingStyle.BEAN) {
            return (Collection) Lists.asList(TailoredObjDiffMethodFactory.class);
        } else if (mappingStyle == MappingStyle.FIELD) {
            return (Collection) Lists.asList(TailoredObjDiffFieldFactory.class);
        } else {
            throw new RuntimeException("not implementation for " + mappingStyle);
        }
    }
}
