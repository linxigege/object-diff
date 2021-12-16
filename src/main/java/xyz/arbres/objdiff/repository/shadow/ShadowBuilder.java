package xyz.arbres.objdiff.repository.shadow;


import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;
import xyz.arbres.objdiff.core.metamodel.type.EnumerableType;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ShadowBuilder {
    private final CdoSnapshot cdoSnapshot;
    private Object shadow;
    private Set<Wiring> wirings = new HashSet<>();

    ShadowBuilder(CdoSnapshot cdoSnapshot, Object shadow) {
        this.cdoSnapshot = cdoSnapshot;
        this.shadow = shadow;
    }

    void withStub(Object shadowStub) {
        this.shadow = shadowStub;
    }

    Object getShadow() {
        return shadow;
    }

    /**
     * nullable
     */
    CdoSnapshot getCdoSnapshot() {
        return cdoSnapshot;
    }

    void addReferenceWiring(ObjDiffProperty property, ShadowBuilder targetShadow) {
        this.wirings.add(new ReferenceWiring(property, targetShadow));
    }

    void addEnumerableWiring(ObjDiffProperty property, Object targetWithShadows) {
        this.wirings.add(new EnumerableWiring(property, targetWithShadows));
    }

    void wire() {
        wirings.forEach(Wiring::wire);
    }

    private abstract class Wiring {
        final ObjDiffProperty property;

        Wiring(ObjDiffProperty property) {
            this.property = property;
        }

        abstract void wire();
    }

    private class ReferenceWiring extends Wiring {
        final ShadowBuilder target;

        ReferenceWiring(ObjDiffProperty property, ShadowBuilder targetShadow) {
            super(property);
            this.target = targetShadow;
        }

        @Override
        void wire() {
            property.set(shadow, target.shadow);
        }
    }

    private class EnumerableWiring extends Wiring {
        final Object targetWithShadows;

        EnumerableWiring(ObjDiffProperty property, Object targetWithShadows) {
            super(property);
            this.targetWithShadows = targetWithShadows;
        }

        @Override
        void wire() {
            EnumerableType propertyType = property.getType();

            Object targetContainer = propertyType.map(targetWithShadows, (valueOrShadow) -> {
                if (valueOrShadow instanceof ShadowBuilder) {
                    //injecting reference to shadow
                    return ((ShadowBuilder) valueOrShadow).shadow;
                }
                return valueOrShadow; //vale is passed as is
            });

            property.set(shadow, targetContainer);
        }
    }
}
