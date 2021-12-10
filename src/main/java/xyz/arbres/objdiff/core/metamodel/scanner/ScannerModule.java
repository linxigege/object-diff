package xyz.arbres.objdiff.core.metamodel.scanner;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.CoreConfiguration;
import xyz.arbres.objdiff.core.MappingStyle;
import xyz.arbres.objdiff.core.pico.LateInstantiatingModule;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class ScannerModule extends LateInstantiatingModule {

    public ScannerModule(CoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {

        MappingStyle mappingStyle = getConfiguration().getMappingStyle();

        Class<? extends PropertyScanner> usedPropertyScanner;
        if (mappingStyle == MappingStyle.BEAN){
            usedPropertyScanner = BeanBasedPropertyScanner.class;
        } else if (mappingStyle == MappingStyle.FIELD){
            usedPropertyScanner = FieldBasedPropertyScanner.class;
        } else{
            throw new RuntimeException("not implementation for "+mappingStyle);
        }

        return (Collection) Lists.asList(
                ClassScanner.class,
                ClassAnnotationsScanner.class,
                AnnotationNamesProvider.class,
                usedPropertyScanner
        );
    }
}
