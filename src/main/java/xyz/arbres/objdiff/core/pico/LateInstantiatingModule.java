package xyz.arbres.objdiff.core.pico;


import org.picocontainer.MutablePicoContainer;
import xyz.arbres.objdiff.core.CoreConfiguration;

/**
 * @author bartosz.walacik
 */
public abstract class LateInstantiatingModule extends InstantiatingModule {

    private final CoreConfiguration configuration;

    public LateInstantiatingModule(CoreConfiguration configuration, MutablePicoContainer container) {
        super(container);
        this.configuration = configuration;
    }

    protected CoreConfiguration getConfiguration() {
        return configuration;
    }
}
