package xyz.arbres.objdiff.common.exception;

/**
 * @author carlos
 * @date 2021/12/7
 * @description Objdiff
 */
public enum ObjDiffExceptionCode {
    /**
     *
     */
    WRONG_USAGE_OF_ObjDiff_AUDITABLE_DELETE("" +
            "The argument '%s' you have passed to a method '%s' annotated with @ObjDiffAuditableDelete " +
            "is not an Entity, nor a Value Object. " +
            "If your intention is to delete by Id, provide the Entity class parameter."),

    WRONG_USAGE_OF_ObjDiff_AUDITABLE_CONDITIONAL_DELETE("" +
            "The return type of the method '%s'\n" +
            "annotated with @ObjDiffAuditableConditionalDelete is not an Entity nor a collection of Entities.\n" +
            "If your intention is to execute shallow delete by condition, the method should return an Entity or a collection of Entities."),

    MALFORMED_ObjDiff_MONGODB_PROPERTIES("Malformed configuration for dedicated MongoDB in ObjDiff.mongodb properties, either host or url mus be defined"),

    CM("Malformed configuration for dedicated MongoDB in ObjDiff.mongodb properties, either host or url mus be defined"),

    ID_TYPE_NOT_SUPPORTED("%s %s can't be used as Id-property type. Problematic class: '%s'."),

    CLASS_EXTRACTION_ERROR(ObjDiffException.BOOTSTRAP_ERROR + "Can't extract Class from Type '%s'."),

    COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED("Committing top-level %ss like '%s' is not supported. You can commit only Entity or ValueObject instance."),

    COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED("Comparing top-level %ss like '%s' is not supported. ObjDiff.compare() is designed to deeply compare two arbitrary complex object graphs. For simple values, equals() does the job."),

    ENTITY_WITHOUT_ID("Class '%s' mapped as Entity has no Id-property. Use @Id annotation to mark unique and not-null Entity identifier"),

    SHALLOW_REF_ENTITY_WITHOUT_ID("Class '%s' mapped as ShallowReference Entity has no Id property. Use @Id annotation to mark unique and not-null Entity identifier"),

    ENTITY_INSTANCE_WITH_NULL_ID("Found Entity instance '%s' with null Id-property '%s'"),

    ENTITY_INSTANCE_WITH_NULL_COMPOSITE_ID("Found Entity instance '%s' with all Id-properties %s nulled"),

    NOT_INSTANCE_OF("Can't create InstanceId for EntityType '%s', class '%s' bounded to EntityType is not assignable from given class '%s'"),

    UNDEFINED_PROPERTY(ObjDiffException.BOOTSTRAP_ERROR + "undefined mandatory property '%s'. Define it in your classpath:ObjDiff.properties"),

    MALFORMED_PROPERTY(ObjDiffException.BOOTSTRAP_ERROR + "unwrap '%s' is invalid for property '%s'. Fix it in your classpath:ObjDiff.properties"),

    CLASSPATH_RESOURCE_NOT_FOUND(ObjDiffException.BOOTSTRAP_ERROR + "classpath resource '%s' could not be found"),

    ALREADY_BUILT(ObjDiffException.BOOTSTRAP_ERROR + "instance already built, each AbstractContainerBuilder may produce only one target instance"),

    PROPERTY_ACCESS_ERROR("error getting value from property '%s' on target object of type '%s', cause: %s"),

    PROPERTY_SETTING_ERROR("error setting '%s' value to property '%s', cause: %s"),

    SETTER_INVOCATION_ERROR("error invoking setter '%s' on target object of type '%s', cause: %s"),

    CONTAINER_NOT_READY(ObjDiffException.BOOTSTRAP_ERROR + "pico container is not ready"),

    AFFECTED_CDO_IS_NOT_AVAILABLE("affected cdo is not available, you can access it only for freshly generated diffs"),

    MISSING_PROPERTY("There is no property '%s' in type '%s'."),

    NOT_IMPLEMENTED("not implemented, %s"),

    IGNORED_AND_INCLUDED_PROPERTIES_MIX("Mapping error in class '%s'. You can either specify Included Properties or Ignored Properties, not both."),

    SNAPSHOT_NOT_FOUND("snapshot '%s' not found in ObjDiffRepository"),

    //graph & snapshot
    VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY("found ValueObject on KEY position in Map property '%s'. Please change the key class mapping to Value or Entity"),

    SNAPSHOT_STATE_VIOLATION("attempt to update snapshot state, property '%s' already added"),

    SNAPSHOT_SERIALIZATION_ERROR("error while serializing snapshot of '%s', duplicated property '%s'"),

    PROPERTY_NOT_FOUND("Property '%s' not found in class '%s'. If the name is correct - check annotations. Properties with @DiffIgnore or @Transient are not visible for ObjDiff."),

    SETTER_NOT_FOUND("setter for getter '%s' not found in class '%s'"),

    /**
     * @since 1.4
     */
    TYPE_NAME_NOT_FOUND(
            "type name '%s' not found. " +
                    "If you are using @TypeName annotation, " +
                    "remember to register this class " +
                    "using ObjDiffBuilder.withPackagesToScan(String) or ObjDiffBuilder.scanTypeName(Class)"),

    MANAGED_CLASS_MAPPING_ERROR("given javaClass '%s' is mapped to %s, expected %s"),

    CLASS_MAPPING_ERROR("given javaClass '%s' is mapped to %s, expected %s"),

    MALFORMED_CHANGE_TYPE_FIELD("no such Change type - '%s'"),

    MALFORMED_ENTRY_CHANGE_TYPE_FIELD("no such EntryChange type - '%s'"),

    CLASS_NOT_MANAGED("given javaClass '%s' is mapped to %s, ManagedType expected"),

    COMPONENT_NOT_FOUND(ObjDiffException.BOOTSTRAP_ERROR + "component of type '%s' not found in container"),

    NO_PUBLIC_CONSTRUCTOR("no public constructor in class '%s'"),

    NO_PUBLIC_ZERO_ARG_CONSTRUCTOR("no public zero-argument constructor in class '%s'"),

    ERROR_WHEN_INVOKING_CONSTRUCTOR("got exception when invoking constructor of class '%s'"),

    CLASS_NOT_FOUND("class not found - '%s'"),

    CLASS_IS_NOT_INSTANCE_OF("given class '%s' is not instance of '%s'"),

    CANT_EXTRACT_CHILD_VALUE_OBJECT(
            "error while extracting child ValueObject from path '%s'" +
                    ", invalid property type, expected ValueObjectType, ContainerType<ValueObjectType> or MapType<?,ValueObjectType>, got '%s'"),

    CANT_PARSE_COMMIT_ID("can't parse given value {'%s'} to CommitId. " +
            "CommitId should consists of two parts : majorId.minorId e.g. 1.0"),

    CANT_DELETE_OBJECT_NOT_FOUND("failed to delete object {'%s'}, " +
            "it doesn't exists in ObjDiffRepository"),

    CANT_FIND_COMMIT_HEAD_ID("can't find commit head id in ObjDiffRepository"),

    SQL_EXCEPTION("%s\nwhile executing sql: %s"),

    UNSUPPORTED_SQL_DIALECT("dialect '%s' is not supported by ObjDiff"),

    MALFORMED_JQL("Invalid JQL query, %s"),

    UNSUPPORTED_OPTIONAL_CONTENT_TYPE("%s is not supported as Optional<> content type"),

    RUNTIME_EXCEPTION("uncategorized runtime exception. %s"),

    TRANSACTION_MANAGER_NOT_SET("Can't create ObjDiff bean due to missing configuration. Since ObjDiff-spring 2.8.0, transactionManager bean should be explicitly provided in TransactionalObjDiffBuilder.withTxManager(). See example at http://ObjDiff.org/documentation/spring-integration/#spring-jpa-example");

    private final String message;

    ObjDiffExceptionCode(String message) {
        this.message = message;
    }

    /**
     * Error description and possibly solution hints.
     */
    public String getMessage() {
        return message;
    }
}
