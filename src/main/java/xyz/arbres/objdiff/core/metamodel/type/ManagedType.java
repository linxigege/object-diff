package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.string.PrettyPrintBuilder;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * ManagedType
 *
 * @author carlos
 * @date 2021-12-07
 */
public abstract class ManagedType extends ClassType {
    private final ManagedClass managedClass;

    ManagedType(ManagedClass managedClass) {
        this(managedClass, Optional.empty());
    }

    ManagedType(ManagedClass managedClass, Optional<String> typeName) {
        super(managedClass.getBaseJavaClass(), typeName);
        this.managedClass = managedClass;
    }

    abstract ManagedType spawn(ManagedClass managedClass, Optional<String> typeName);


    @Override
    protected Type getRawDehydratedType() {
        return GlobalId.class;
    }


    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addMultiField("managedProperties", managedClass.getManagedProperties());
    }

    /**
     * @throws
     */
    public ObjDiffProperty getProperty(String propertyName) {
        return managedClass.getProperty(propertyName);
    }

    public Optional<ObjDiffProperty> findProperty(String propertyName) {
        return managedClass.hasProperty(propertyName) ?
                Optional.of(managedClass.getProperty(propertyName)) :
                Optional.empty();
    }

    public List<ObjDiffProperty> getProperties(Predicate<ObjDiffProperty> query) {
        return managedClass.getManagedProperties(query);
    }

    /**
     * unmodifiable list
     */
    public List<ObjDiffProperty> getProperties() {
        return managedClass.getManagedProperties();
    }

    public void forEachProperty(Consumer<ObjDiffProperty> consumer) {
        managedClass.forEachProperty(consumer);
    }

    public Set<String> getPropertyNames() {
        return managedClass.getPropertyNames();
    }

    ManagedClass getManagedClass() {
        return managedClass;
    }
}
