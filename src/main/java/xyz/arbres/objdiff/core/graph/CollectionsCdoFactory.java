package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.collections.Lists;
import xyz.arbres.objdiff.common.reflection.ObjDiffMember;
import xyz.arbres.objdiff.core.metamodel.object.UnboundedValueObjectId;
import xyz.arbres.objdiff.core.metamodel.property.Property;
import xyz.arbres.objdiff.core.metamodel.scanner.ClassScanner;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffProperty;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;
import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

/**
 * @author pawelszymczyk
 */
public class CollectionsCdoFactory {

    private final ClassScanner classScanner;
    private final TailoredObjDiffMemberFactory memberGenericTypeInjector;
    private final TypeMapper typeMapper;

    public CollectionsCdoFactory(ClassScanner classScanner, TailoredObjDiffMemberFactory memberGenericTypeInjector, TypeMapper typeMapper) {
        this.classScanner = classScanner;
        this.memberGenericTypeInjector = memberGenericTypeInjector;
        this.typeMapper = typeMapper;
    }

    public LiveCdo createCdo(final CollectionWrapper wrapper, final Class<?> clazz) {
        Property primaryProperty = classScanner.scan(wrapper.getClass()).getProperties().get(0);
        ObjDiffMember ObjDiffMember = memberGenericTypeInjector.create(primaryProperty, clazz);

        Property fixedProperty = new Property(ObjDiffMember);
        ObjDiffProperty fixedJProperty = new ObjDiffProperty(() -> typeMapper.getPropertyType(fixedProperty), fixedProperty);

        ValueObjectType valueObject = new ValueObjectType(wrapper.getClass(), Lists.asList(fixedJProperty));
        return new LiveCdoWrapper(wrapper, new UnboundedValueObjectId(valueObject.getName()), valueObject);
    }
}
