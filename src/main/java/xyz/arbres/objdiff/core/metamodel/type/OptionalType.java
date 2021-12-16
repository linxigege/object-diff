package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.EnumerableFunction;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.EnumerationAwareOwnerContext;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author bartosz.walacik
 */
public class OptionalType extends CollectionType {

    /**
     * for TypeFactory.spawnFromPrototype()
     */
    public OptionalType(Type baseJavaType, TypeMapperLazy typeMapperLazy) {
        super(baseJavaType, typeMapperLazy);
    }

    public OptionalType(TypeMapperLazy typeMapperLazy) {
        super(Optional.class, typeMapperLazy);
    }

    @Override
    public Object map(Object sourceOptional_, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(sourceOptional_, mapFunction);
        Optional sourceOptional = (Optional) sourceOptional_;
        return sourceOptional.map(o -> mapFunction.apply(o, new EnumerationAwareOwnerContext(owner)));
    }

    @Override
    public Object map(Object sourceOptional_, Function mapFunction) {
        Validate.argumentsAreNotNull(sourceOptional_, mapFunction);
        Optional sourceOptional = (Optional) sourceOptional_;
        return sourceOptional.map(o -> mapFunction.apply(o));
    }

    @Override
    protected Stream<Object> items(Object source) {
        if (source == null) {
            return Stream.empty();
        }
        Optional sourceOptional = (Optional) source;
        return (Stream) sourceOptional.map(it -> Stream.of(it)).orElse(Stream.empty());
    }

    @Override
    public boolean isEmpty(Object optional) {
        return optional == null || !((Optional) optional).isPresent();
    }

    @Override
    public Object empty() {
        return Optional.empty();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Optional.class;
    }
}
