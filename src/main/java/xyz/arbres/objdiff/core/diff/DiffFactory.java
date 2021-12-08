package xyz.arbres.objdiff.core.diff;



import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.CommitMetadata;
import xyz.arbres.objdiff.core.diff.graph.ObjectGraph;
import xyz.arbres.objdiff.core.metamodel.type.ObjDiffType;
import xyz.arbres.objdiff.core.metamodel.type.TypeMapper;
import xyz.arbres.objdiff.core.metamodel.type.ValueType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Maciej Zasada
 * @author Bartosz Walacik
 */
public class DiffFactory {

    private final NodeMatcher nodeMatcher = new NodeMatcher();
    private final TypeMapper typeMapper;
    private final List<NodeChangeAppender> nodeChangeAppenders;
    private final List<PropertyChangeAppender> propertyChangeAppender;
    private final LiveGraphFactory graphFactory;
    private final CoreConfiguration ObjDiffCoreConfiguration;

    public DiffFactory(TypeMapper typeMapper, List<NodeChangeAppender> nodeChangeAppenders, List<PropertyChangeAppender> propertyChangeAppender, LiveGraphFactory graphFactory, CoreConfiguration ObjDiffCoreConfiguration) {
        this.typeMapper = typeMapper;
        this.nodeChangeAppenders = nodeChangeAppenders;
        this.graphFactory = graphFactory;
        this.ObjDiffCoreConfiguration = ObjDiffCoreConfiguration;

        //sort by priority
        Collections.sort(propertyChangeAppender, (p1, p2) -> ((Integer)p1.priority()).compareTo(p2.priority()));
        this.propertyChangeAppender = propertyChangeAppender;
    }

    /**
     * @see ObjDiff#compare(Object, Object)
     */
    public Diff compare(Object oldVersion, Object currentVersion) {
        return create(buildGraph(oldVersion), buildGraph(currentVersion), Optional.<CommitMetadata>empty());
    }

    public Diff create(ObjectGraph leftGraph, ObjectGraph rightGraph, Optional<CommitMetadata> commitMetadata) {
        Validate.argumentsAreNotNull(leftGraph, rightGraph);

        GraphPair graphPair = new GraphPair(leftGraph, rightGraph, commitMetadata);
        return createAndAppendChanges(graphPair);
    }

    private ObjectGraph buildGraph(Object handle) {
        if (handle == null) {
            return new EmptyGraph();
        }

        ObjDiffType jType = typeMapper.getObjDiffType(handle.getClass());
        if (jType instanceof ValueType || jType instanceof PrimitiveType){
            throw new ObjDiffException(ObjDiffExceptionCode.COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED,
                    jType.getClass().getSimpleName(), handle.getClass().getSimpleName());
        }
        return graphFactory.createLiveGraph(handle);
    }

    /**
     * Graph scope appender
     */
    private Diff createAndAppendChanges(GraphPair graphPair) {
        DiffBuilder diff = new DiffBuilder(ObjDiffCoreConfiguration.getPrettyValuePrinter());

        //calculate node scope diff
        for (NodeChangeAppender appender : nodeChangeAppenders) {
            diff.addChanges(appender.getChangeSet(graphPair));
        }

        //calculate snapshot of NewObjects and RemovedObjects
        if (ObjDiffCoreConfiguration.isInitialChanges()) {
            for (ObjectNode node : graphPair.getOnlyOnRight()) {
                NodePair pair = new NodePair(new FakeNode(node.getCdo()), node, graphPair.getCommitMetadata());
                appendPropertyChanges(diff, pair);
            }
        }

        if (ObjDiffCoreConfiguration.isTerminalChanges()) {
            for (ObjectNode node : graphPair.getOnlyOnLeft()) {
                NodePair pair = new NodePair(node, new FakeNode(node.getCdo()), graphPair.getCommitMetadata());
                appendPropertyChanges(diff, pair);
            }
        }

        //calculate property-to-property diff
        for (NodePair pair : nodeMatcher.match(graphPair)) {
            appendPropertyChanges(diff, pair);
        }

        return diff.build();
    }

    /**
     * delegates to {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public LiveGraph createLiveGraph(Object handle) {
        Object wrappedHandle = wrapTopLevelContainer(handle);

        return new ObjectGraphBuilder(typeMapper, liveCdoFactory).buildGraph(wrappedHandle);
    }
}
