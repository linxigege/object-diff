package xyz.arbres.objdiff.core.pico;


import org.picocontainer.PicoContainer;
import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.reflection.ArgumentResolver;

/**
 * @author bartosz walacik
 */
public class ContainerArgumentResolver implements ArgumentResolver {

    private final PicoContainer container;

    public ContainerArgumentResolver(PicoContainer container) {
        this.container = container;
    }

    @Override
    public Object resolve(Class argType) {
        if (argType == PicoContainer.class){
            return container;
        }
        Object component = container.getComponent(argType);

        if (component == null) {
            throw new ObjDiffException(ObjDiffExceptionCode.COMPONENT_NOT_FOUND, argType.getName());
        }

        return component;
    }
}
