package xyz.arbres.objdiff.core.metamodel.type;


import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.core.metamodel.object.EnumerationAwareOwnerContext;
import xyz.arbres.objdiff.core.metamodel.object.GlobalId;
import xyz.arbres.objdiff.core.metamodel.object.OwnerContext;

/**
 * @author bartosz.walacik
 */
public class MapEnumerationOwnerContext extends EnumerationAwareOwnerContext {
    private Object key;
    private boolean isKey;

    private final ObjDiffType keyType;
    private final ObjDiffType valueType;

    public static MapEnumerationOwnerContext dummy(KeyValueType keyValueType) {
            return new MapEnumerationOwnerContext(keyValueType,
                new OwnerContext() {
                @Override
                public GlobalId getOwnerId() {
                    throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
                }

                @Override
                public String getPath() {
                    throw new ObjDiffException(ObjDiffExceptionCode.NOT_IMPLEMENTED);
                }

                @Override
                public boolean requiresObjectHasher() {
                    return false;
                }
            });
    }

    public MapEnumerationOwnerContext(KeyValueType keyValueType, OwnerContext ownerContext) {
        this(keyValueType, ownerContext, false);
    }

    public MapEnumerationOwnerContext(KeyValueType keyValueType, OwnerContext ownerContext, boolean requiresObjectHasher) {
        super(ownerContext, requiresObjectHasher);
        this.keyType = keyValueType.getKeyObjDiffType();
        this.valueType = keyValueType.getValueObjDiffType();
    }

    @Override
    public String getEnumeratorContextPath() {
        if (key != null) {
            return key.toString();
        }
        return "";
    }

    public boolean isKey() {
        return isKey;
    }

    public ObjDiffType getCurrentType() {
        if (isKey) {
            return keyType;
        }
        return valueType;
    }

    public void switchToValue(Object key) {
        this.key = key;
        this.isKey = false;
    }

    public void switchToKey() {
        this.key = null;
        this.isKey = true;
    }
}
