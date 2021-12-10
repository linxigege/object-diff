package xyz.arbres.objdiff.core.diff.appenders;

import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.core.CoreConfiguration;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.pico.LateInstantiatingModule;

import java.util.Collection;

public class DiffAppendersModule extends LateInstantiatingModule {

    private final Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender;

    public DiffAppendersModule(CoreConfiguration ObjDiffCoreConfiguration, MutablePicoContainer container) {
        super(ObjDiffCoreConfiguration, container);
        this.listChangeAppender = ObjDiffCoreConfiguration.getListCompareAlgorithm().getAppenderClass();
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                NewObjectAppender.class,
                MapChangeAppender.class,
                CollectionAsListChangeAppender.class,
                listChangeAppender,
                SetChangeAppender.class,
                ArrayChangeAppender.class,
                ObjectRemovedAppender.class,
                ReferenceChangeAppender.class,
                OptionalChangeAppender.class,
                ValueChangeAppender.class
        );
    }
}
