package xyz.arbres.objdiff.core.commit;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.Changes;
import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * ObjDiff commit is a similar concept to GIT commit.
 * It records snapshots of <b>changed</b> domain objects.
 * <br><br>
 * <p>
 * One commit can affect one or more domain objects.
 * <br><br>
 * <p>
 * Commit holds following data:
 * <ul>
 *     <li>who did change the data - {@link CommitMetadata#getAuthor()} </li>
 *     <li>when the change was made - {@link CommitMetadata#getCommitDate()} </li>
 *     <li>list of atomic changes between two domain object graphs - {@link #getChanges()}</li>
 *     <li>list of Snapshots of <b>affected</b> objects - {@link #getSnapshots()}</li>
 * </ul>
 *
 * @author bartosz walacik
 */
public final class Commit {

    private final CommitMetadata commitMetadata;
    private final List<CdoSnapshot> snapshots;
    private final Diff diff;

    Commit(CommitMetadata commitMetadata, List<CdoSnapshot> snapshots, Diff diff) {
        Validate.argumentsAreNotNull(commitMetadata, snapshots, diff);
        this.commitMetadata = commitMetadata;
        this.snapshots = snapshots;
        this.diff = diff;
    }

    /**
     * Monotonically increasing id,
     * e.g. 1.0, 2.0, ...
     */
    public CommitId getId() {
        return commitMetadata.getId();
    }

    public String getAuthor() {
        return commitMetadata.getAuthor();
    }

    public Map<String, String> getProperties() {
        return commitMetadata.getProperties();
    }

    Diff getDiff() {
        return diff;
    }

    /**
     * Commit creation timestamp in local time zone
     */
    public LocalDateTime getCommitDate() {
        return commitMetadata.getCommitDate();
    }

    /**
     * Commit creation timestamp in UTC.
     * <br/><br/>
     * <p>
     * Since 5.1, commitDateInstant is persisted in ObjDiffRepository
     * to provide reliable chronological ordering, especially when
     * is used.
     * <p>
     * <br/><br/>
     * <p>
     * Commits persisted by ObjDiff older then 5.1
     * have commitDateInstant guessed from commitDate and current {@link TimeZone}
     *
     * @since 5.1
     */
    public Instant getCommitDateInstant() {
        return commitMetadata.getCommitDateInstant();
    }

    /**
     * @return unmodifiableList
     */
    public List<CdoSnapshot> getSnapshots() {
        return Collections.unmodifiableList(snapshots);
    }

    /**
     * @return unmodifiableList
     */
    public Changes getChanges() {
        return diff.getChanges();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Commit(id:" + commitMetadata.getId());
        b.append(", snapshots:" + snapshots.size());
        b.append(", author:" + commitMetadata.getAuthor());
        b.append(", " + diff.changesSummary());
        b.append(")");
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Commit other = (Commit) o;

        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
