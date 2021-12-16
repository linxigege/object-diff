package xyz.arbres.objdiff.core.changelog;

import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.changetype.*;
import xyz.arbres.objdiff.core.diff.changetype.container.ArrayChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ContainerChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.diff.changetype.container.SetChange;
import xyz.arbres.objdiff.core.diff.changetype.map.MapChange;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;

public interface ChangeProcessor<T> {
    public void onCommit(CommitMetadata commitMetadata);

    void onAffectedObject(GlobalId globalId);

    void beforeChangeList();

    void afterChangeList();

    /**
     * called before each change
     */
    void beforeChange(Change change);

    /**
     * called after each change
     */
    void afterChange(Change change);

    /**
     * called on {@link ValueChange}, {@link ReferenceChange},
     * {@link ContainerChange} and {@link MapChange}
     */
    void onPropertyChange(PropertyChange propertyChange);

    void onValueChange(ValueChange valueChange);

    void onReferenceChange(ReferenceChange referenceChange);

    void onNewObject(NewObject newObject);

    void onObjectRemoved(ObjectRemoved objectRemoved);

    /**
     * called on {@link ListChange}, {@link SetChange} and {@link ArrayChange}
     */
    void onContainerChange(ContainerChange containerChange);

    void onSetChange(SetChange setChange);

    void onArrayChange(ArrayChange arrayChange);

    void onListChange(ListChange listChange);

    void onMapChange(MapChange mapChange);

    /**
     * should return processing result, for example a change log
     */
    T result();
}
