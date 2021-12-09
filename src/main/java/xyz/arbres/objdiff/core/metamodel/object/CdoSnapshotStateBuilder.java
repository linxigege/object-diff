package xyz.arbres.objdiff.core.metamodel.object;



import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.metamodel.property.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotStateBuilder {
    private final Map<String, Object> properties = new HashMap<>();

    private CdoSnapshotStateBuilder(){
    }

    public static CdoSnapshotStateBuilder cdoSnapshotState(){
        return new CdoSnapshotStateBuilder();
    }

    public CdoSnapshotStateBuilder withPropertyValue(String propertyName, Object value){
        Validate.argumentIsNotNull(propertyName);
        if (value == null){
            return this;
        }

        if (properties.containsKey(propertyName)){
            throw new ObjDiffException(ObjDiffExceptionCode.SNAPSHOT_STATE_VIOLATION, propertyName);
        }

        properties.put(propertyName, value);
        return this;
    }

    public CdoSnapshotStateBuilder withPropertyValue(Property property, Object value){
        return withPropertyValue(property.getName(), value);
    }

    public boolean contains(Property property) {
        return properties.containsKey(property.getName());
    }

    public CdoSnapshotState build() {
        return new CdoSnapshotState(properties);
    }
}
