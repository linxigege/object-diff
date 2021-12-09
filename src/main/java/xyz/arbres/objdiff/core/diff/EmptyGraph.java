package xyz.arbres.objdiff.core.diff;


import xyz.arbres.objdiff.core.graph.ObjectGraph;

import java.util.Collections;

class EmptyGraph extends ObjectGraph {
    EmptyGraph() {
        super(Collections.emptySet());
    }
}

