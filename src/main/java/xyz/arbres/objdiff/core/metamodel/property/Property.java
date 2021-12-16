package xyz.arbres.objdiff.core.metamodel.property;

import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.reflection.ObjDiffMember;

import java.lang.reflect.Type;
import java.util.Optional;

import static xyz.arbres.objdiff.common.validation.Validate.argumentIsNotNull;

/**
 * Property
 *
 * @author carlos
 * @date 2021-12-08
 */
public class Property {
    public static final String ID_ANN = "Id";
    public static final String EMBEDDED_ID_ANN = "EmbeddedId";

    private transient final ObjDiffMember member;
    private transient final boolean hasTransientAnn;
    private transient final boolean hasIncludedAnn;
    private transient final boolean hasShallowReferenceAnn;
    private final String name;
    private final String originalName;

    public Property(ObjDiffMember member, boolean hasTransientAnn, boolean hasShallowReferenceAnn, Optional<String> name, boolean hasIncludedAnn) {
        argumentIsNotNull(member);
        this.member = member;
        this.hasTransientAnn = hasTransientAnn;
        this.hasShallowReferenceAnn = hasShallowReferenceAnn;
        this.originalName = member.propertyName();
        this.name = name.orElse(originalName);
        this.hasIncludedAnn = hasIncludedAnn;
    }

    public Property(ObjDiffMember member, boolean hasTransientAnn, boolean hasShallowReferenceAnn, String name, boolean hasIncludedAnn) {
        this(member, hasTransientAnn, hasShallowReferenceAnn, Optional.of(name), hasIncludedAnn);
    }

    public Property(ObjDiffMember member) {
        this(member, false, false, Optional.empty(), false);
    }

    public Type getGenericType() {
        return member.getGenericResolvedType();
    }

    public Class<?> getDeclaringClass() {
        return member.getDeclaringClass();
    }

    /**
     * use getGenericType() when possible, see ObjDiffMember.resolvedReturnType
     */
    public Class<?> getRawType() {
        return member.getRawType();
    }

    /**
     * true if property looks like identifier of an Entity, for example has @Id annotation
     */
    public boolean looksLikeId() {
        return member.looksLikeId();
    }

    /**
     * Returns property value, even if private.
     * <p>
     * If there is no this property in target -- returns {@link MissingProperty#INSTANCE}
     *
     * @param target invocation target
     */
    public Object get(Object target) {
        return member.getEvenIfPrivate(target);
    }

    /**
     * Sets property value, even if private.
     * <br/>
     * Swallows ObjDiffException.MISSING_PROPERTY
     *
     * @param target invocation target
     * @param value  value to be set
     */
    public void set(Object target, Object value) {
        try {
            member.setEvenIfPrivate(target, value);
        } catch (ObjDiffException e) {
            if (e.getCode() == ObjDiffExceptionCode.MISSING_PROPERTY) {
                return; //swallowed
            }
            throw e;
        }
    }

    public boolean isNull(Object target) {
        return get(target) == null;
    }

    /**
     * Property name used by ObjDiff, originalName by default, can be changed with @PropertyName.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Property name as in Java class
     */
    public String getOriginalName() {
        return originalName;
    }

    public boolean hasCustomName() {
        return this.name != this.originalName;
    }

    public boolean hasTransientAnn() {
        return hasTransientAnn;
    }

    public boolean isHasIncludedAnn() {
        return hasIncludedAnn;
    }

    public boolean hasShallowReferenceAnn() {
        return hasShallowReferenceAnn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Property that = (Property) o;
        return member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return member.hashCode();
    }

    @Override
    public String toString() {
        return member.toString();
    }

    public ObjDiffMember getMember() {
        return member;
    }
}
