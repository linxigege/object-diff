package xyz.arbres.objdiff.repository.sql;


import xyz.arbres.objdiff.common.validation.Validate;

/**
* @author bartosz walacik
*/
public final class ValueObjectIdDTO extends GlobalIdDTO {
    private final GlobalIdDTO ownerIdDTO;
    private final String path;

    ValueObjectIdDTO(Class ownerClass, Object ownerLocalId, String path) {
        Validate.argumentsAreNotNull(ownerClass, ownerLocalId, path);
        ownerIdDTO = InstanceIdDTO.instanceId(ownerLocalId, ownerClass);
        this.path = path;
    }

    ValueObjectIdDTO(Class ownerClass, String path) {
        Validate.argumentsAreNotNull(ownerClass, path);
        ownerIdDTO = UnboundedValueObjectIdDTO.unboundedValueObjectId(ownerClass);
        this.path = path;
    }

    public static ValueObjectIdDTO valueObjectId(Object ownerLocalId, Class ownerClass, String fragment){
        return new ValueObjectIdDTO(ownerClass, ownerLocalId, fragment);
    }

    public static ValueObjectIdDTO withUnboundedValueObjectOwner(Class ownerClass, String fragment){
        return new ValueObjectIdDTO(ownerClass, fragment);
    }

    @Override
    public String value() {
        return ownerIdDTO.value()+"#"+ path;
    }

    public GlobalIdDTO getOwnerIdDTO() {
        return ownerIdDTO;
    }

    public String getPath() {
        return path;
    }
}
