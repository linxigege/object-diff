package xyz.arbres.objdiff.core.commit;

import xyz.arbres.objdiff.common.validation.Validate;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * CommitMetadata
 *
 * @author carlos
 * @date 2021-12-07
 */
public class CommitMetadata implements Serializable {

    private final String author;
    private final Map<String, String> properties;
    private final LocalDateTime commitDate;
    private final Instant commitDateInstant;
    private final CommitId id;

    public CommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate, Instant commitDateInstant, CommitId id) {

        Validate.argumentsAreNotNull(author, properties, commitDate, id);
        this.author = author;
        this.properties = new HashMap<>(properties);
        this.commitDate = commitDate;
        this.commitDateInstant = initCommitDateInstant(commitDate,commitDateInstant);
        this.id = id;
    }

    public static CommitMetadata nullObject() {
        return NullCommitMetadata.instance();
    }

    private Instant initCommitDateInstant(LocalDateTime commitDate, Instant commitDateInstant) {
        if (commitDateInstant != null) {
            return commitDateInstant;
        }

        //for old records without commitDateInstant
        return commitDate.toInstant(ZonedDateTime.now().getOffset());
    }
}
