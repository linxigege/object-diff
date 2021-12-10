package xyz.arbres.objdiff.repository.shadow;


import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.json.JsonConverter;
import xyz.arbres.objdiff.core.metamodel.object.CdoSnapshot;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;

import java.util.function.BiFunction;

/**
 * @author bartosz.walacik
 */
public class ShadowFactory {

    private final JsonConverter jsonConverter;
    private final TypeMapper typeMapper;

    public ShadowFactory(JsonConverter jsonConverter, TypeMapper typeMapper) {
        this.jsonConverter = jsonConverter;
        this.typeMapper = typeMapper;
    }

    public Shadow createShadow(CdoSnapshot cdoSnapshot, CommitMetadata rootContext, BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver) {
        ShadowGraphBuilder builder = new ShadowGraphBuilder(jsonConverter, referenceResolver, typeMapper, rootContext);
        return new Shadow(rootContext, builder.buildDeepShadow(cdoSnapshot));
    }

    Object createShadow(CdoSnapshot cdoSnapshot) {
        return createShadow(cdoSnapshot, cdoSnapshot.getCommitMetadata(), (source, target) -> null).get();
    }

    Object createShadow(CdoSnapshot cdoSnapshot, BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver) {
        return createShadow(cdoSnapshot, cdoSnapshot.getCommitMetadata(), referenceResolver).get();
    }
}
