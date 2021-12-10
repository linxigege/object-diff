package xyz.arbres.objdiff.core;


import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.pico.InstantiatingModule;
import xyz.arbres.objdiff.core.pico.ObjDiffModule;

import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class AbstractContainerBuilder {

    private MutablePicoContainer container;

    protected <T> T getContainerComponent(Class<T> ofClass) {
        checkIfBuilt();
        return container.getComponent(ofClass);
    }

    protected void bootContainer() {
        container = new DefaultPicoContainer(new Caching());
    }

    protected void addModule(InstantiatingModule module) {
        checkIfBuilt();
        module.instantiateAndBindComponents();
    }

    protected void addModule(ObjDiffModule module) {
        checkIfBuilt();
        for (Class component : module.getComponents()) {
            addComponent(component);
        }
    }

    protected <T> List<T> getComponents(Class<T> ofClass){
        checkIfBuilt();
        return container.getComponents(ofClass);
    }

    protected MutablePicoContainer getContainer() {
        return container;
    }

    protected void addComponent(Object classOrInstance) {
        checkIfBuilt();
        container.addComponent(classOrInstance);
    }

    protected void bindComponent(Object componentKey, Object implementationOrInstance) {
        checkIfBuilt();
        container.addComponent(componentKey, implementationOrInstance);
    }

    protected void removeComponent(Object classOrInstance) {
        checkIfBuilt();
        container.removeComponent(classOrInstance);
    }

    private void checkIfNotBuilt() {
        if (isBuilt()) {
            throw new ObjDiffException(ObjDiffExceptionCode.ALREADY_BUILT);
        }
    }

    private void checkIfBuilt() {
        if (!isBuilt()) {
            throw new ObjDiffException(ObjDiffExceptionCode.CONTAINER_NOT_READY);
        }
    }

    private boolean isBuilt() {
        return container != null;
    }
}
