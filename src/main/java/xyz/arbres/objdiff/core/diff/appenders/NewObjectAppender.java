package xyz.arbres.objdiff.core.diff.appenders;

import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.GraphPair;
import xyz.arbres.objdiff.core.diff.changetype.NewObject;
import xyz.arbres.objdiff.core.metamodel.type.ValueObjectType;

import java.util.Set;
import java.util.stream.Collectors;

class NewObjectAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return (Set) graphPair.getOnlyOnRight().stream()
                .filter(it -> !(it.getManagedType() instanceof ValueObjectType))
                .map(input -> new NewObject(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}

