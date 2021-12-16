package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.collections.EnumerableFunction;
import xyz.arbres.objdiff.common.collections.Maps;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author bartosz walacik
 */
public class MapType extends KeyValueType {

    public MapType(Type baseJavaType, TypeMapperLazy typeMapperlazy) {
        super(baseJavaType, 2, typeMapperlazy);
    }

    public static void mapEntrySet(KeyValueType keyValueType,
                                   Collection<Map.Entry<?, ?>> sourceEntries,
                                   EnumerableFunction mapFunction,
                                   MapEnumerationOwnerContext mapEnumerationContext,
                                   BiConsumer entryConsumer,
                                   boolean filterNulls) {
        for (Map.Entry entry : sourceEntries) {
            //key
            mapEnumerationContext.switchToKey();
            Object mappedKey = mapFunction.apply(entry.getKey(), mapEnumerationContext);
            if (mappedKey == null && filterNulls) continue;

            //value
            mapEnumerationContext.switchToValue(mappedKey);

            Object mappedValue = null;
            if (keyValueType.getValueObjDiffType() instanceof ContainerType) {
                ContainerType containerType = (ContainerType) keyValueType.getValueObjDiffType();
                mappedValue = containerType.map(entry.getValue(), mapFunction, mapEnumerationContext);
            } else {
                mappedValue = mapFunction.apply(entry.getValue(), mapEnumerationContext);
            }

            entryConsumer.accept(mappedKey, mappedValue);
        }
    }

    public static void mapEntrySet(KeyValueType keyValueType,
                                   Collection<Map.Entry<?, ?>> sourceEntries,
                                   Function mapFunction,
                                   BiConsumer entryConsumer,
                                   boolean filterNulls) {
        MapEnumerationOwnerContext enumeratorContext = MapEnumerationOwnerContext.dummy(keyValueType);
        EnumerableFunction enumerableFunction = (input, ownerContext) -> mapFunction.apply(input);
        mapEntrySet(keyValueType, sourceEntries, enumerableFunction, enumeratorContext, entryConsumer, filterNulls);
    }

    /**
     * @return immutable Map
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        Validate.argumentsAreNotNull(mapFunction, owner);

        Map sourceMap = Maps.wrapNull(sourceEnumerable);
        Map targetMap = new HashMap(sourceMap.size());
        MapEnumerationOwnerContext enumeratorContext = new MapEnumerationOwnerContext(this, owner);

        mapEntrySet(this, sourceMap.entrySet(), mapFunction, enumeratorContext, (k, v) -> targetMap.put(k, v), false);

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public Object map(Object source, Function mapFunction, boolean filterNulls) {
        Validate.argumentsAreNotNull(mapFunction);

        Map sourceMap = Maps.wrapNull(source);
        Map targetMap = new HashMap(sourceMap.size());

        mapEntrySet(this, sourceMap.entrySet(), mapFunction, (k, v) -> targetMap.put(k, v), filterNulls);

        return Collections.unmodifiableMap(targetMap);
    }

    @Override
    public boolean isEmpty(Object map) {
        return map == null || ((Map) map).isEmpty();
    }

    @Override
    public Object empty() {
        return Collections.emptyMap();
    }

    @Override
    protected Stream<Map.Entry> entries(Object source) {
        Map sourceMap = Maps.wrapNull(source);
        return sourceMap.entrySet().stream();
    }

    @Override
    public Class<?> getEnumerableInterface() {
        return Map.class;
    }
}
