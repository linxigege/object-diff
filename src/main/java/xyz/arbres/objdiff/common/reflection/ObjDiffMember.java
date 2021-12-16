package xyz.arbres.objdiff.common.reflection;

import xyz.arbres.objdiff.common.collections.Sets;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.property.MissingProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ObjDiffMember
 *
 * @author carlos
 * @date 2021-12-08
 */
public abstract class ObjDiffMember<T extends Member> {
    private final T rawMember; //delegate
    private final Optional<Type> resolvedReturnType;
    private final boolean looksLikeId;

    private final Map<Class, Optional<ObjDiffMember>> mirrorMembersMemoized = new ConcurrentHashMap<>();

    protected ObjDiffMember(T rawMember, Type resolvedReturnType) {
        this(rawMember, resolvedReturnType, ReflectionUtil.looksLikeId(rawMember));
    }

    protected ObjDiffMember(final T rawMember, final Type resolvedReturnType, final boolean looksLikeId) {
        Validate.argumentIsNotNull(rawMember);
        this.rawMember = rawMember;
        this.resolvedReturnType = Optional.ofNullable(resolvedReturnType);
        this.looksLikeId = looksLikeId;
        setAccessibleIfNecessary(rawMember);
    }

    protected abstract Type getRawGenericType();

    public abstract Class<?> getRawType();

    public T getRawMember() {
        return rawMember;
    }

    public Type getGenericResolvedType() {
        if (resolvedReturnType.isPresent()) {
            return resolvedReturnType.get();
        }
        return getRawGenericType();
    }

    public Class<?> getDeclaringClass() {
        return rawMember.getDeclaringClass();
    }

    public String name() {
        return rawMember.getName();
    }

    public String propertyName() {
        return rawMember.getName();
    }

    public Set<Annotation> getAnnotations() {
        return ReflectionUtil.getAnnotations(rawMember);
    }

    public Set<Class<? extends Annotation>> getAnnotationTypes() {
        return Sets.transform(getAnnotations(), ann -> ann.annotationType());
    }

    public boolean looksLikeId() {
        return looksLikeId;
    }

    public abstract Object getEvenIfPrivate(Object target);

    public abstract void setEvenIfPrivate(Object target, Object value);

    void setAccessibleIfNecessary(Member rawMember) {
        if (!isPublic(rawMember)) {
            ((AccessibleObject) rawMember).setAccessible(true); //that's Java Reflection API ...
        }
    }

    private boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjDiffMember that = (ObjDiffMember) o;

        return this.rawMember.equals(that.rawMember) && this.resolvedReturnType.equals(that.resolvedReturnType);
    }

    @Override
    public int hashCode() {
        return rawMember.hashCode() + resolvedReturnType.hashCode();
    }

    Object getOnMissingProperty(Object onObject) {
        Optional<ObjDiffMember> mirror = mirrorMembersMemoized.computeIfAbsent(onObject.getClass(),
                c -> ReflectionUtil.getMirrorMember(this, c));

        return mirror.map(s -> s.getEvenIfPrivate(onObject)).orElse(MissingProperty.INSTANCE);
    }

    public abstract String memberType();
}
