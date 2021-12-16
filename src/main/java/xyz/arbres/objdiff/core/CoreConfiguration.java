package xyz.arbres.objdiff.core;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.core.commit.CommitId;
import xyz.arbres.objdiff.core.diff.ListCompareAlgorithm;

import java.util.function.Supplier;

/**
 * @author bartosz walacik
 */
public class CoreConfiguration {

    private final PrettyValuePrinter prettyValuePrinter;

    private final MappingStyle mappingStyle;

    private final ListCompareAlgorithm listCompareAlgorithm;
    private final boolean initialChanges;
    private final boolean terminalChanges;
    private final CommitIdGenerator commitIdGenerator;
    private final Supplier<CommitId> customCommitIdGenerator;
    private boolean prettyPrint;

    CoreConfiguration(PrettyValuePrinter prettyValuePrinter, MappingStyle mappingStyle, ListCompareAlgorithm listCompareAlgorithm, boolean initialChanges, CommitIdGenerator commitIdGenerator, Supplier<CommitId> customCommitIdGenerator, boolean terminalChanges, boolean prettyPrint) {
        this.prettyValuePrinter = prettyValuePrinter;
        this.mappingStyle = mappingStyle;
        this.listCompareAlgorithm = listCompareAlgorithm;
        this.initialChanges = initialChanges;
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = customCommitIdGenerator;
        this.terminalChanges = terminalChanges;
        this.prettyPrint = prettyPrint;
    }

    public PrettyValuePrinter getPrettyValuePrinter() {
        return prettyValuePrinter;
    }

    public MappingStyle getMappingStyle() {
        return mappingStyle;
    }

    public ListCompareAlgorithm getListCompareAlgorithm() {
        return listCompareAlgorithm;
    }

    public boolean isInitialChanges() {
        return initialChanges;
    }

    public boolean isTerminalChanges() {
        return terminalChanges;
    }

    public CommitIdGenerator getCommitIdGenerator() {
        return commitIdGenerator;
    }

    public Supplier<CommitId> getCustomCommitIdGenerator() {
        return customCommitIdGenerator;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }
}
