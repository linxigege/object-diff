package xyz.arbres.objdiff.core.json.typeadapter.change;


import xyz.arbres.objdiff.core.diff.changetype.PropertyChangeMetadata;
import xyz.arbres.objdiff.core.diff.changetype.container.ArrayChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerElementChange;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
class ArrayChangeTypeAdapter extends ContainerChangeTypeAdapter<ArrayChange> {

    public ArrayChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        return new ArrayChange(metadata, changes);
    }

    @Override
    public Class getValueType() {
        return ArrayChange.class;
    }
}


