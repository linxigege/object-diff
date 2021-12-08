package xyz.arbres.objdiff.core.commit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * NullCommitMetadata
 *
 * @author carlos
 * @date 2021-12-07
 */
public class NullCommitMetadata extends CommitMetadata{
    public NullCommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate, Instant commitDateInstant, CommitId id) {
        super(author, properties, commitDate, commitDateInstant, id);
    }
    static NullCommitMetadata instance(){
        return new NullCommitMetadata("anonymous", Collections.emptyMap(),LocalDateTime.now(),Instant.now(),new CommitId(0,0));
    }
}
