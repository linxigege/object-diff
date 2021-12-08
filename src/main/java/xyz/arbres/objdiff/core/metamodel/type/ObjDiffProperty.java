package xyz.arbres.objdiff.core.metamodel.type;

import xyz.arbres.objdiff.common.string.ToStringBuilder;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.util.function.Supplier;

/**
 * ObjDiffProperty
 *
 * @author carlos
 * @date 2021-12-08
 */
public class ObjDiffProperty extends Property {

    /**
     * Supplier prevents stack overflow exception when building ObjDiffType
     */
    private final Supplier<ObjDiffType> propertyType;

    public ObjDiffProperty(Supplier<ObjDiffType> propertyType, Property property) {
        super(property.getMember(),  property.hasTransientAnn(), property.hasShallowReferenceAnn(), property.getName(), property.isHasIncludedAnn());
        this.propertyType = propertyType;
    }

    public <T extends ObjDiffType> T getType() {
        return (T) propertyType.get();
    }

    public boolean isEntityType() {
        return getType() instanceof EntityType;
    }

    public boolean isValueObjectType() {
        return getType() instanceof ValueObjectType;
    }

    public boolean isPrimitiveOrValueType() {
        return getType() instanceof PrimitiveOrValueType;
    }

    public boolean isCustomType() {
        return getType() instanceof CustomType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjDiffProperty that = (ObjDiffProperty) o;
        return super.equals(that) && this.getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isShallowReference(){
        return (hasShallowReferenceAnn()
                || getType() instanceof ShallowReferenceType);
    }

    @Override
    public String toString() {
        return getMember().memberType() + " " +
                getType().getClass().getSimpleName() + ":" +
                ToStringBuilder.typeName(getMember().getGenericResolvedType()) + " " +
                getName() + (getMember().memberType().equals("Getter") ? "()" : "")+
                ", declared in " + getDeclaringClass().getSimpleName();
    }
}
