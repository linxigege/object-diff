package xyz.arbres.objdiff.repository.sql;

import xyz.arbres.objdiff.common.validation.Validate;

public final class InstanceIdDTO extends GlobalIdDTO {
    private final Class javaClass;
    private final Object localId;

    InstanceIdDTO(Class javaClass, Object localId) {
        Validate.argumentsAreNotNull(javaClass, localId);
        this.javaClass = javaClass;
        this.localId = localId;
    }

    public static InstanceIdDTO instanceId(Object localId, Class javaClass) {
        Validate.argumentsAreNotNull(localId, javaClass);
        return new InstanceIdDTO(javaClass, localId);
    }

    @Override
    public String value() {
        return javaClass.getName() + "/" + localId;
    }

    public Class getEntity() {
        return javaClass;
    }

    public Object getCdoId() {
        return localId;
    }

}
