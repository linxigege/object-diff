package xyz.arbres.objdiff.repository.shadow;


import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitId;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.repository.api.ObjDiffRepository;

/**
 * Shadow is a historical version of a domain object restored
 * from a snapshot loaded from {@link ObjDiffRepository}.
 * <br/><br/>
 * <p>
 * Shadows use the same types as domain objects.
 * For example, a Shadow of a Person object is an instance of Person.class.
 * <br/><br/>
 * <p>
 * Shadows class is a thin wrapper for a Shadow object and {@link CommitMetadata}
 *
 * @param <T> type of a domain object
 * @author bartosz.walacik
 */
public class Shadow<T> {
    private final CommitMetadata commitMetadata;
    private final T it;

    Shadow(CommitMetadata commitMetadata, T shadow) {
        Validate.argumentsAreNotNull(commitMetadata, shadow);
        this.commitMetadata = commitMetadata;
        this.it = shadow;
    }

    public CommitMetadata getCommitMetadata() {
        return commitMetadata;
    }

    public CommitId getCommitId() {
        return commitMetadata.getId();
    }

    /**
     * Shadow object per se
     */
    public T get() {
        return it;
    }

    @Override
    public String toString() {
        return "Shadow{" +
                "it=" + it +
                ", commitMetadata=" + commitMetadata +
                '}';
    }
}
