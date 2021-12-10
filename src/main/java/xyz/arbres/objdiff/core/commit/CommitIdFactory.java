package xyz.arbres.objdiff.core.commit;


import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.CommitIdGenerator;
import xyz.arbres.objdiff.core.CoreConfiguration;
import xyz.arbres.objdiff.repository.api.ObjDiffExtendedRepository;

class CommitIdFactory {
    private final CoreConfiguration ObjDiffCoreConfiguration;
    private final ObjDiffExtendedRepository ObjDiffRepository;
    private final CommitSeqGenerator commitSeqGenerator;
    private final DistributedCommitSeqGenerator distributedCommitSeqGenerator;

    CommitIdFactory(CoreConfiguration ObjDiffCoreConfiguration, ObjDiffExtendedRepository ObjDiffRepository, CommitSeqGenerator commitSeqGenerator, DistributedCommitSeqGenerator distributedCommitSeqGenerator) {
        this.ObjDiffCoreConfiguration = ObjDiffCoreConfiguration;
        this.ObjDiffRepository = ObjDiffRepository;
        this.commitSeqGenerator = commitSeqGenerator;
        this.distributedCommitSeqGenerator = distributedCommitSeqGenerator;
    }

    CommitId nextId() {
        if (ObjDiffCoreConfiguration.getCommitIdGenerator() == CommitIdGenerator.SYNCHRONIZED_SEQUENCE) {
            CommitId head = ObjDiffRepository.getHeadId();
            return commitSeqGenerator.nextId(head);
        }

        if (ObjDiffCoreConfiguration.getCommitIdGenerator() == CommitIdGenerator.RANDOM) {
            return distributedCommitSeqGenerator.nextId();
        }

        if (ObjDiffCoreConfiguration.getCommitIdGenerator() == CommitIdGenerator.CUSTOM) {
            return ObjDiffCoreConfiguration.getCustomCommitIdGenerator().get();
        }

        throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
    }
}
