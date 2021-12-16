package xyz.arbres.objdiff.core.graph;


import xyz.arbres.objdiff.common.collections.EnumerableFunction;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;
import xyz.arbres.objdiff.core.metamodel.object.PropertyOwnerContext;
import xyz.arbres.objdiff.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
class EdgeBuilder {
    private final TypeMapper typeMapper;
    private final NodeReuser nodeReuser;
    private final LiveCdoFactory cdoFactory;

    EdgeBuilder(TypeMapper typeMapper, NodeReuser nodeReuser, LiveCdoFactory cdoFactory) {
        this.typeMapper = typeMapper;
        this.nodeReuser = nodeReuser;
        this.cdoFactory = cdoFactory;
    }

    /**
     * @return node stub, could be redundant so check reuse context
     */
    AbstractSingleEdge buildSingleEdge(ObjectNode node, ObjDiffProperty singleRef) {
        Object rawReference = node.getPropertyValue(singleRef);
        OwnerContext ownerContext = createOwnerContext(node, singleRef);

        if (!singleRef.isShallowReference()) {
            LiveCdo cdo = cdoFactory.create(rawReference, ownerContext);
            LiveNode targetNode = buildNodeStubOrReuse(cdo);
            return new SingleEdge(singleRef, targetNode);
        }
        return new ShallowSingleEdge(singleRef, cdoFactory.createId(rawReference, ownerContext));
    }

    private OwnerContext createOwnerContext(ObjectNode parentNode, ObjDiffProperty property) {
        return new PropertyOwnerContext(parentNode.getGlobalId(), property.getName());
    }

    AbstractMultiEdge createMultiEdge(ObjDiffProperty containerProperty, EnumerableType enumerableType, ObjectNode node) {
        OwnerContext owner = createOwnerContext(node, containerProperty);

        Object container = node.getPropertyValue(containerProperty);

        boolean isShallow = containerProperty.isShallowReference() ||
                hasShallowReferenceItems(enumerableType);

        EnumerableFunction itemMapper = (input, context) -> {

            if (context instanceof MapEnumerationOwnerContext) {
                // corner case, for Maps with primitive-or-value keys
                MapEnumerationOwnerContext mapContext = (MapEnumerationOwnerContext) context;
                if (!(mapContext.getCurrentType() instanceof ManagedType)) {
                    return input;
                }
            }

            if (!isShallow) {
                LiveCdo cdo = cdoFactory.create(input, context);
                return buildNodeStubOrReuse(cdo);
            } else {
                return cdoFactory.createId(input, context);
            }
        };

        Object mappedEnumerable = enumerableType.map(container, itemMapper, owner);

        if (!isShallow) {
            return new MultiEdge(containerProperty, mappedEnumerable);
        } else {
            return new ShallowMultiEdge(containerProperty, mappedEnumerable);
        }
    }

    private boolean hasShallowReferenceItems(EnumerableType enumerableType) {
        if (enumerableType instanceof ContainerType) {
            ContainerType containerType = (ContainerType) enumerableType;
            return containerType.getItemObjDiffType() instanceof ShallowReferenceType;
        }
        if (enumerableType instanceof KeyValueType) {
            KeyValueType keyValueType = (KeyValueType) enumerableType;
            return keyValueType.getKeyObjDiffType() instanceof ShallowReferenceType ||
                    keyValueType.getValueObjDiffType() instanceof ShallowReferenceType;
        }
        return false;
    }

    private LiveNode buildNodeStubOrReuse(LiveCdo cdo) {
        if (nodeReuser.isReusable(cdo)) {
            return nodeReuser.getForReuse(cdo);
        } else {
            return buildNodeStub(cdo);
        }
    }

    LiveNode buildNodeStub(LiveCdo cdo) {
        LiveNode newStub = new LiveNode(cdo);
        nodeReuser.enqueueStub(newStub);
        return newStub;
    }
}
