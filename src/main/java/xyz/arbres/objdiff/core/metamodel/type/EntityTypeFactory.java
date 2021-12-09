package xyz.arbres.objdiff.core.metamodel.type;



import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.metamodel.clazz.EntityDefinition;
import xyz.arbres.objdiff.core.metamodel.scanner.ClassScan;

import java.util.List;

/**
 * @author bartosz.walacik
 */
class EntityTypeFactory {
    private final ManagedClassFactory managedClassFactory;

    EntityTypeFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    EntityType createEntity(EntityDefinition definition, ClassScan scan) {
        ManagedClass managedClass = managedClassFactory.create(definition, scan);

        List<ObjDiffProperty> idProperties;
        if (definition.hasExplicitId()) {
            idProperties = managedClass.getProperties(definition.getIdPropertyNames());
        } else {
            idProperties = findDefaultIdProperties(managedClass, definition.isShallowReference());
        }

        if (definition.isShallowReference()) {
            return new ShallowReferenceType(managedClass, idProperties, definition.getTypeName());
        } else {
            return new EntityType(managedClass, idProperties, definition.getTypeName());
        }
    }

    /**
     * @throws ObjDiffException ENTITY_WITHOUT_ID
     */
    private List<ObjDiffProperty> findDefaultIdProperties(ManagedClass managedClass, boolean isShallowReference) {
        if (managedClass.getLooksLikeId().isEmpty()) {
            ObjDiffExceptionCode code = isShallowReference ?
                    ObjDiffExceptionCode.SHALLOW_REF_ENTITY_WITHOUT_ID :
                    ObjDiffExceptionCode.ENTITY_WITHOUT_ID;
            throw new ObjDiffException(code, managedClass.getBaseJavaClass().getName());
        }
        return managedClass.getLooksLikeId();
    }
}
