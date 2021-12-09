package xyz.arbres.objdiff.core.diff;


import xyz.arbres.objdiff.core.diff.appenders.PropertyChangeAppender;
import xyz.arbres.objdiff.core.diff.appenders.SimpleListChangeAppender;
import xyz.arbres.objdiff.core.diff.appenders.levenshtein.LevenshteinListChangeAppender;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;

public enum ListCompareAlgorithm {

    SIMPLE(SimpleListChangeAppender.class),
    LEVENSHTEIN_DISTANCE(LevenshteinListChangeAppender.class),
    AS_SET(ListAsSetChangeAppender.class);

    private final Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender;

    ListCompareAlgorithm(Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender) {
        this.listChangeAppender = listChangeAppender;
    }

    public Class<? extends PropertyChangeAppender<ListChange>> getAppenderClass() {
        return listChangeAppender;
    }
}
