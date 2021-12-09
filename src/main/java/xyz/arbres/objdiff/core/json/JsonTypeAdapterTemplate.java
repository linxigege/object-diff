package xyz.arbres.objdiff.core.json;


import xyz.arbres.objdiff.common.collections.Lists;

import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class JsonTypeAdapterTemplate<T> implements JsonTypeAdapter<T>{
    public abstract Class getValueType();

    @Override
    public List<Class> getValueTypes() {
        return Lists.immutableListOf(getValueType());
    }
}
