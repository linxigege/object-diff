package xyz.arbres.objdiff.core.diff.appenders;

import xyz.arbres.objdiff.core.diff.changetype.container.ContainerElementChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ElementValueChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ValueAdded;
import xyz.arbres.objdiff.core.diff.changetype.container.ValueRemoved;
import xyz.arbres.objdiff.core.diff.changetype.map.EntryAdded;
import xyz.arbres.objdiff.core.diff.changetype.map.EntryChange;
import xyz.arbres.objdiff.core.diff.changetype.map.EntryRemoved;
import xyz.arbres.objdiff.core.diff.changetype.map.EntryValueChange;

import java.util.function.Function;

/**
 * MapChangesToListChangesFunction
 *
 * @author carlos
 * @date 2021-12-09
 */
public class MapChangesToListChangesFunction implements Function<EntryChange, ContainerElementChange> {

    @Override
    public ContainerElementChange apply(EntryChange input) {
        int index = (int)input.getKey();
        if (input instanceof EntryAdded) {
            return new ValueAdded(index, ((EntryAdded) input).getValue());
        } else if (input instanceof EntryRemoved) {
            return new ValueRemoved(index, ((EntryRemoved) input).getValue());
        } else if (input instanceof EntryValueChange) {
            return new ElementValueChange(index, ((EntryValueChange) input).getLeftValue(),
                    ((EntryValueChange) input).getRightValue());
        }

        throw new IllegalArgumentException("Unknown change type: " + input.getClass().getSimpleName());
    }
}
