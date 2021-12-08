package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.common.string.PrettyPrintBuilder;
import xyz.arbres.objdiff.common.string.ToStringBuilder;
import xyz.arbres.objdiff.common.validation.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * ObjDiffType
 *
 * @author carlos
 * @date 2021-12-07
 */
public abstract class ObjDiffType {

    public static final Class DEFAULT_TYPE_PARAMETER=Object.class;

    private final Type baseJavaType;

    private final List<Type> concreteTypeArguments;

    private final String name;

    ObjDiffType(Type baseJavaType) {
        this(baseJavaType, Optional.empty(), 0);
    }

    ObjDiffType(Type baseJavaType, Optional<String> name, int expectedArgs) {
        Validate.argumentsAreNotNull(baseJavaType, name);
        this.baseJavaType = baseJavaType;
        this.name = name.orElse(baseJavaType.getTypeName());
        this.concreteTypeArguments = Collections.unmodifiableList(
                buildListOfConcreteTypeArguments(baseJavaType, expectedArgs));
    }

    ObjDiffType spawn(Type baseJavaType) {
        try {
            Constructor c = this.getClass().getConstructor(spawnConstructorArgTypes());
            return (ObjDiffType)c.newInstance(spawnConstructorArgs(baseJavaType));
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("error calling Constructor for " + this.getClass().getName(), exception);
        }
    }

    protected Object[] spawnConstructorArgs(Type baseJavaType) {
        return new Object[]{baseJavaType};
    }

    protected Class[] spawnConstructorArgTypes() {
        return new Class[]{Type.class};
    }

    public boolean isGenericType() {
        return (baseJavaType instanceof ParameterizedType);
    }

    public Type getBaseJavaType() {
        return baseJavaType;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isInstance(Object cdo);

    public abstract boolean canBePrototype();

    /**
     * Used for comparing as Values
     */
    public boolean equals(Object left, Object right) {
        return Objects.equals(left, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjDiffType)) {
            return false;
        }

        ObjDiffType that = (ObjDiffType) o;
        return baseJavaType.equals(that.baseJavaType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "baseType", baseJavaType);
    }

    @Override
    public int hashCode() {
        return baseJavaType.hashCode();
    }

    public List<Type> getConcreteClassTypeArguments() {
        return concreteTypeArguments;
    }

    public final String prettyPrint(){
        return prettyPrintBuilder().build();
    }

    protected PrettyPrintBuilder prettyPrintBuilder(){
        return new PrettyPrintBuilder(this)
                .addField("baseType", baseJavaType)
                .addField("typeName", getName());
    }

    private static List<Type> buildListOfConcreteTypeArguments(Type baseJavaType, int expectedSize) {

        List<Type> allTypeArguments = ReflectionUtil.getAllTypeArguments(baseJavaType);

        List<Type> concreteTypeArguments = new ArrayList<>(expectedSize);

        for (int i=0; i<expectedSize; i++) {
            Type existingArgument = null;
            if (!allTypeArguments.isEmpty() && i<allTypeArguments.size()){
                existingArgument = allTypeArguments.get(i);
            }
            concreteTypeArguments.add(getActualClassTypeArgument(existingArgument));
        }

        return concreteTypeArguments;
    }

    private static Type getActualClassTypeArgument(Type existingArgument) {
        if (existingArgument == null) {
            return DEFAULT_TYPE_PARAMETER;
        }

        Optional<Type> concreteType = ReflectionUtil.isConcreteType(existingArgument);
        if (concreteType.isPresent()) {
            return concreteType.get();
        } else {
            return DEFAULT_TYPE_PARAMETER;
        }
    }
}
