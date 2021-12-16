package xyz.arbres.objdiff.core;


import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.repository.api.ObjDiffRepository;

import java.util.Comparator;

/**
 * @author bartosz.walacik
 */
public enum CommitIdGenerator {
    /**
     * Generates neat, sequential commit identifiers.
     * Based on {@link ObjDiffRepository#getHeadId()}.
     * <br/><br/>
     * <p>
     * Should not be used in distributed applications.
     */
    SYNCHRONIZED_SEQUENCE {
        @Override
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDateInstant)
                    .thenComparing(CommitMetadata::getId);
        }
    },

    /**
     * Non-blocking algorithm based on UUID.
     * <br/><br/>
     * <p>
     * Suitable for distributed applications.<br/>
     *
     * <b>Warning!</b> When RANDOM generator is set,
     * Shadow query runner sorts commits by commitDateInstant.
     * It means, that Shadow queries would be correct only
     * if all application servers have synchronized clocks.
     */
    RANDOM {
        @Override
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDateInstant);
        }
    },

    /**
     * Provided by user
     */
    CUSTOM {
        @Override
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDateInstant);
        }
    };

    public abstract Comparator<CommitMetadata> getComparator();
}
