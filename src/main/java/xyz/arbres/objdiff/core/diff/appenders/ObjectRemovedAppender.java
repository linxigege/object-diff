package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.GraphPair;
import xyz.arbres.objdiff.core.diff.changetype.ObjectRemoved;
import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

import java.util.Set;
import java.util.stream.Collectors;

class ObjectRemovedAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return (Set) graphPair.getOnlyOnLeft().stream()
                .filter(it -> !(it.getManagedType() instanceof ValueObjectType))
                .map(input -> new ObjectRemoved(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}
