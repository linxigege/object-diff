package xyz.arbres.objdiff.core;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitId;
import xyz.arbres.objdiff.core.diff.ListCompareAlgorithm;

import java.util.function.Supplier;

class CoreConfigurationBuilder {
    private PrettyValuePrinter prettyValuePrinter = PrettyValuePrinter.getDefault();

    private MappingStyle mappingStyle = MappingStyle.FIELD;

    private ListCompareAlgorithm listCompareAlgorithm = ListCompareAlgorithm.SIMPLE;

    private boolean prettyPrint = true;

    private boolean initialChanges = true;

    private boolean terminalChanges = true;

    private CommitIdGenerator commitIdGenerator = CommitIdGenerator.SYNCHRONIZED_SEQUENCE;

    private Supplier<CommitId> customCommitIdGenerator;

    private CoreConfigurationBuilder() {
    }

    static CoreConfigurationBuilder coreConfiguration() {
        return new CoreConfigurationBuilder();
    }

    CoreConfiguration build() {
        return new CoreConfiguration(
                prettyValuePrinter,
                mappingStyle,
                listCompareAlgorithm,
                initialChanges,
                commitIdGenerator,
                customCommitIdGenerator,
                terminalChanges,
                prettyPrint
        );
    }

    CoreConfigurationBuilder withPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }

    CoreConfigurationBuilder withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    CoreConfigurationBuilder withCommitIdGenerator(CommitIdGenerator commitIdGenerator) {
        Validate.argumentIsNotNull(commitIdGenerator);
        Validate.argumentCheck(commitIdGenerator != CommitIdGenerator.CUSTOM, "use withCustomCommitIdGenerator(Supplier<CommitId>)");
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = null;
        return this;
    }

    CoreConfigurationBuilder withCustomCommitIdGenerator(Supplier<CommitId> customCommitIdGenerator) {
        Validate.argumentIsNotNull(customCommitIdGenerator);
        this.commitIdGenerator = CommitIdGenerator.CUSTOM;
        this.customCommitIdGenerator = customCommitIdGenerator;
        return this;
    }

    CoreConfigurationBuilder withInitialChanges(boolean initialChanges) {
        this.initialChanges = initialChanges;
        return this;
    }

    CoreConfigurationBuilder withTerminalChanges(boolean terminalChanges) {
        this.terminalChanges = terminalChanges;
        return this;
    }

    CoreConfigurationBuilder withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        Validate.argumentIsNotNull(algorithm);
        this.listCompareAlgorithm = algorithm;
        return this;
    }

    CoreConfigurationBuilder withPrettyPrintDateFormats(ObjDiffCoreProperties.PrettyPrintDateFormats prettyPrintDateFormats) {
        Validate.argumentIsNotNull(prettyPrintDateFormats);
        prettyValuePrinter = new PrettyValuePrinter(prettyPrintDateFormats);
        return this;
    }
}
