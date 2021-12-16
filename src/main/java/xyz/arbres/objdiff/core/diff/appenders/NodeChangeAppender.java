package xyz.arbres.objdiff.core.diff.appenders;


import xyz.arbres.objdiff.core.diff.Change;
import xyz.arbres.objdiff.core.diff.GraphPair;

import java.util.Set;

/**
 * Node scope change appender (NewObject & ObjectRemoved)
 */
public interface NodeChangeAppender {

    Set<Change> getChangeSet(GraphPair graphPair);

}
