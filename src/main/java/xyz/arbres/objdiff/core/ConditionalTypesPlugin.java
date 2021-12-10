package xyz.arbres.objdiff.core;


import xyz.arbres.objdiff.core.diff.appenders.PropertyChangeAppender;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapperLazy;

import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz.walacik
 */
public abstract class ConditionalTypesPlugin  {

    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders() {
        return Collections.emptyList();
    }

    public Collection<ObjDiffType> getNewTypes(TypeMapperLazy typeMapperLazy) {
        return Collections.emptyList();
    }
}
