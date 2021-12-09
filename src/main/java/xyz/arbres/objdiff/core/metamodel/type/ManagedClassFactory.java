package xyz.arbres.objdiff.core.metamodel.type;



import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.core.metamodel.annotation.DiffIgnore;
import xyz.arbres.objdiff.core.metamodel.clazz.ClientsClassDefinition;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.scanner.ClassScan;

import java.util.List;


/**
 * @author bartosz walacik
 */
class ManagedClassFactory {
    private final TypeMapper typeMapper;

    public ManagedClassFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    ManagedClass create(ClientsClassDefinition def, ClassScan scan) {
        List<ObjDiffProperty> allProperties = convert(scan.getProperties());

        ManagedPropertiesFilter managedPropertiesFilter =
                new ManagedPropertiesFilter(def.getBaseJavaClass(), allProperties, def.getPropertiesFilter());

        return create(def.getBaseJavaClass(), allProperties, managedPropertiesFilter);
    }

    ManagedClass createFromPrototype(Class<?> baseJavaClass, ClassScan scan, ManagedPropertiesFilter prototypePropertiesFilter) {
        List<ObjDiffProperty> allProperties = convert(scan.getProperties());
        return create(baseJavaClass, allProperties, prototypePropertiesFilter);
    }

    private ManagedClass create(Class<?> baseJavaClass, List<ObjDiffProperty> allProperties, ManagedPropertiesFilter propertiesFilter){

        List<ObjDiffProperty> filtered = propertiesFilter.filterProperties(allProperties);

        filtered = filterIgnoredType(filtered, baseJavaClass);

        return new ManagedClass(baseJavaClass, filtered,
                Lists.positiveFilter(allProperties, p -> p.looksLikeId()), propertiesFilter);
    }

    private List<ObjDiffProperty> convert(List<Property> properties) {
        return Lists.transform(properties, p -> {
            if (typeMapper.contains(p.getGenericType())) {
                final ObjDiffType ObjDiffType = typeMapper.getObjDiffType(p.getGenericType());
                return new ObjDiffProperty(() -> ObjDiffType, p);
            }
            return new ObjDiffProperty(() -> typeMapper.getObjDiffType(p.getGenericType()), p);
        });
    }

    private List<ObjDiffProperty> filterIgnoredType(List<ObjDiffProperty> properties, final Class<?> currentClass){

        return Lists.negativeFilter(properties, property -> {
            if (property.getRawType() == currentClass){
                return false;
            }
            //prevents stackoverflow
            if (typeMapper.contains(property.getRawType()) ||
                typeMapper.contains(property.getGenericType())) {
                return typeMapper.getObjDiffType(property.getRawType()) instanceof IgnoredType;
            }

            return ReflectionUtil.isAnnotationPresentInHierarchy(property.getRawType(), DiffIgnore.class);
        });
    }
}
