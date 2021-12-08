package xyz.arbres.objdiff.core;


import xyz.arbres.objdiff.common.string.PrettyValuePrinter;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.Change;

import java.util.Collections;
import java.util.List;

/**
 * List of Changes done in a specific commit.
 *
 * <ul>
 * <li/>{@link #getCommit()}} commit metadata
 * <li/>{@link #get()} list of Changes
 * </ul>
 */
public final class ChangesByCommit {
    private final List<Change> changes;
    private final CommitMetadata commitMetadata;
    private final transient PrettyValuePrinter valuePrinter;

    ChangesByCommit(CommitMetadata commitMetadata, List<Change> changes, PrettyValuePrinter valuePrinter) {
        Validate.argumentsAreNotNull(commitMetadata, changes, valuePrinter);
        this.changes = new Changes(changes, valuePrinter);
        this.commitMetadata = commitMetadata;
        this.valuePrinter = valuePrinter;
    }

    /**
     * Prints the nicely formatted list of changes in a given commit.
     * Alias to {@link #toString()}.
     */
    public final String prettyPrint() {
        return toString();
    }

    /**
     * Changes grouped by entities
     */
    public List<ChangesByObject> groupByObject() {
        return new Changes(changes, valuePrinter).groupByObject();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("Commit " + commitMetadata.getId() +
                " done by " + commitMetadata.getAuthor() +
                " at " + valuePrinter.format(commitMetadata.getCommitDate()) +
                " :\n");


        groupByObject().forEach(it -> b.append(it.toString()));

        return b.toString();
    }

    public List<Change> get() {
        return Collections.unmodifiableList(changes);
    }

    public CommitMetadata getCommit() {
        return commitMetadata;
    }
}
