package xyz.arbres.objdiff.core;

import xyz.arbres.objdiff.common.date.DateProvider;
import xyz.arbres.objdiff.common.date.DefaultDateProvider;
import xyz.arbres.objdiff.common.reflection.ReflectionUtil;
import xyz.arbres.objdiff.common.validation.Validate;
import xyz.arbres.objdiff.core.commit.Commit;
import xyz.arbres.objdiff.core.commit.CommitFactoryModule;
import xyz.arbres.objdiff.core.diff.Diff;
import xyz.arbres.objdiff.core.diff.DiffFactoryModule;
import xyz.arbres.objdiff.core.diff.ListCompareAlgorithm;
import xyz.arbres.objdiff.core.diff.appenders.DiffAppendersModule;
import xyz.arbres.objdiff.core.diff.changetype.InitialValueChange;
import xyz.arbres.objdiff.core.diff.changetype.NewObject;
import xyz.arbres.objdiff.core.diff.changetype.ValueChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ListChange;
import xyz.arbres.objdiff.core.diff.changetype.container.ValueAdded;
import xyz.arbres.objdiff.core.diff.customer.CustomPropertyComparator;
import xyz.arbres.objdiff.core.diff.customer.CustomToNativeAppenderAdapter;
import xyz.arbres.objdiff.core.diff.customer.CustomValueComparator;
import xyz.arbres.objdiff.core.graph.GraphFactoryModule;
import xyz.arbres.objdiff.core.graph.TailoredObjDiffMemberFactoryModule;
import xyz.arbres.objdiff.core.json.JsonConverter;
import xyz.arbres.objdiff.core.json.JsonConverterBuilder;
import xyz.arbres.objdiff.core.json.JsonTypeAdapter;
import xyz.arbres.objdiff.core.json.typeadapter.change.ChangeTypeAdaptersModule;
import xyz.arbres.objdiff.core.json.typeadapter.commit.CommitTypeAdaptersModule;
import xyz.arbres.objdiff.core.json.typeadapter.commit.DiffTypeDeserializer;
import xyz.arbres.objdiff.core.metamodel.annotation.DiffIgnore;
import xyz.arbres.objdiff.core.metamodel.annotation.TypeName;
import xyz.arbres.objdiff.core.metamodel.clazz.*;
import xyz.arbres.objdiff.core.metamodel.scanner.ScannerModule;
import xyz.arbres.objdiff.core.metamodel.type.*;
import xyz.arbres.objdiff.core.snapshot.SnapshotModule;
import xyz.arbres.objdiff.repository.api.ConfigurationAware;
import xyz.arbres.objdiff.repository.api.ObjDiffExtendedRepository;
import xyz.arbres.objdiff.repository.api.ObjDiffRepository;
import xyz.arbres.objdiff.repository.inmemory.InMemoryRepository;
import xyz.arbres.objdiff.repository.shadow.ShadowModule;
import xyz.arbres.objdiff.repository.sql.JqlModule;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static xyz.arbres.objdiff.common.validation.Validate.argumentIsNotNull;
import static xyz.arbres.objdiff.common.validation.Validate.argumentsAreNotNull;


/**
 * Creates a ObjDiff instance based on your domain model metadata and custom configuration.
 * <br/><br/>
 * <p>
 * For example, to build a ObjDiff instance configured with reasonable defaults:
 * <pre>
 * ObjDiff ObjDiff = ObjDiffBuilder.ObjDiff().build();
 * </pre>
 * <p>
 * To build a ObjDiff instance with an Entity type:
 * <pre>
 * ObjDiff ObjDiff = ObjDiffBuilder.ObjDiff()
 *                              .registerEntity(MyEntity.class)
 *                              .build();
 * </pre>
 *
 * @author bartosz walacik
 * @see <a href="http://ObjDiff.org/documentation/domain-configuration/">http://ObjDiff.org/documentation/domain-configuration</a>
 */
public class ObjDiffBuilder extends AbstractContainerBuilder {

    private final Map<Class, ClientsClassDefinition> clientsClassDefinitions = new LinkedHashMap<>();

    private final Map<Class, Function<Object, String>> mappedToStringFunction = new ConcurrentHashMap<>();

    private final Set<Class> classesToScan = new HashSet<>();

    private final Set<ConditionalTypesPlugin> conditionalTypesPlugins;

    private CoreConfigurationBuilder coreConfigurationBuilder = CoreConfigurationBuilder.coreConfiguration();
    private ObjDiffRepository repository;
    private DateProvider dateProvider;
    private long bootStart = System.currentTimeMillis();

    private IgnoredClassesStrategy ignoredClassesStrategy;

    /**
     * use static factory method {@link ObjDiffBuilder#ObjDiff()}
     */
    protected ObjDiffBuilder() {

        //conditional plugins
        conditionalTypesPlugins = new HashSet<>();


        // bootstrap pico container & core module
        bootContainer();
        addModule(new CoreObjDiffModule(getContainer()));
    }

    public static ObjDiffBuilder ObjDiff() {
        return new ObjDiffBuilder();
    }

    public ObjDiff build() {

        ObjDiff ObjDiff = assembleObjDiffInstance();
        repository.ensureSchema();

        long boot = System.currentTimeMillis() - bootStart;
        return ObjDiff;
    }

    protected ObjDiff assembleObjDiffInstance() {
        CoreConfiguration coreConfiguration = configurationBuilder().build();
        addComponent(coreConfiguration);

        // boot main modules
        addModule(new DiffFactoryModule());
        addModule(new CommitFactoryModule(getContainer()));
        addModule(new SnapshotModule(getContainer()));
        addModule(new GraphFactoryModule(getContainer()));
        addModule(new DiffAppendersModule(coreConfiguration, getContainer()));
        addModule(new TailoredObjDiffMemberFactoryModule(coreConfiguration, getContainer()));
        addModule(new ScannerModule(coreConfiguration, getContainer()));
        addModule(new ShadowModule(getContainer()));
        addModule(new JqlModule(getContainer()));

        // boot TypeMapper module
        addComponent(new DynamicMappingStrategy(ignoredClassesStrategy));
        addModule(new TypeMapperModule(getContainer()));

        // boot add-ons modules
        Set<ObjDiffType> additionalTypes = bootAddOns();

        // boot JSON beans & domain aware typeAdapters
        bootJsonConverter();

        bootDateTimeProvider();

        // clases to scan & additionalTypes
        for (Class c : classesToScan) {
            typeMapper().getObjDiffType(c);
        }
        typeMapper().addPluginTypes(additionalTypes);

        mapRegisteredClasses();

        bootRepository();

        return getContainerComponent(ObjDiffCore.class);
    }

    /**
     * @see <a href="http://ObjDiff.org/documentation/repository-configuration">http://ObjDiff.org/documentation/repository-configuration</a>
     */
    public ObjDiffBuilder registerObjDiffRepository(ObjDiffRepository repository) {

        this.repository = repository;
        return this;
    }

    /**
     * Registers an {@link EntityType}. <br/>
     * Use @Id annotation to mark exactly one Id-property.
     * <br/><br/>
     * <p>
     * Optionally, use @Transient or @{@link DiffIgnore} annotations to mark ignored properties.
     * <br/><br/>
     * <p>
     * For example, Entities are: Person, Document
     *
     * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#entity">http://ObjDiff.org/documentation/domain-configuration/#entity</a>
     * @see #registerEntity(EntityDefinition)
     */
    public ObjDiffBuilder registerEntity(Class<?> entityClass) {

        return registerEntity(new EntityDefinition(entityClass));
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Optionally, use @Transient or @{@link DiffIgnore} annotations to mark ignored properties.
     * <br/><br/>
     * <p>
     * For example, ValueObjects are: Address, Point
     *
     * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#value-object">http://ObjDiff.org/documentation/domain-configuration/#value-object</a>
     * @see #registerValueObject(ValueObjectDefinition)
     */
    public ObjDiffBuilder registerValueObject(Class<?> valueObjectClass) {

        registerType(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    /**
     * Registers an {@link EntityType}. <br/>
     * Use this method if you are not willing to use {@link Entity} annotation.
     * <br/></br/>
     * <p>
     * Recommended way to create {@link EntityDefinition} is {@link EntityDefinitionBuilder},
     * for example:
     * <pre>
     * ObjDiffBuilder.registerEntity(
     *     EntityDefinitionBuilder.entityDefinition(Person.class)
     *     .withIdPropertyName("id")
     *     .withTypeName("Person")
     *     .withIgnoredProperties("notImportantProperty","transientProperty")
     *     .build());
     * </pre>
     * <p>
     * For simple cases, you can use {@link EntityDefinition} constructors,
     * for example:
     * <pre>
     * ObjDiffBuilder.registerEntity( new EntityDefinition(Person.class, "login") );
     * </pre>
     *
     * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#entity">http://ObjDiff.org/documentation/domain-configuration/#entity</a>
     * @see EntityDefinitionBuilder#entityDefinition(Class)
     */
    public ObjDiffBuilder registerEntity(EntityDefinition entityDefinition) {

        return registerType(entityDefinition);
    }

    /**
     * Generic version of {@link #registerEntity(EntityDefinition)} and
     * {@link #registerValueObject(ValueObjectDefinition)}
     */
    public ObjDiffBuilder registerType(ClientsClassDefinition clientsClassDefinition) {

        clientsClassDefinitions.put(clientsClassDefinition.getBaseJavaClass(), clientsClassDefinition);
        return this;
    }

    public ObjDiffBuilder registerTypes(Collection<ClientsClassDefinition> clientsClassDefinitions) {

        clientsClassDefinitions.forEach(it -> registerType(it));
        return this;
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Use this method if you are not willing to use {@link ValueObject} annotations.
     * <br/></br/>
     * <p>
     * Recommended way to create {@link ValueObjectDefinition} is {@link ValueObjectDefinitionBuilder}.
     * For example:
     * <pre>
     * ObjDiffBuilder.registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Address.class)
     *     .withIgnoredProperties(ignoredProperties)
     *     .withTypeName(typeName)
     *     .build();
     * </pre>
     * <p>
     * For simple cases, you can use {@link ValueObjectDefinition} constructors,
     * for example:
     * <pre>
     * ObjDiffBuilder.registerValueObject( new ValueObjectDefinition(Address.class, "ignored") );
     * </pre>
     *
     * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#value-object">http://ObjDiff.org/documentation/domain-configuration/#value-object</a>
     * @see ValueObjectDefinitionBuilder#valueObjectDefinition(Class)
     */
    public ObjDiffBuilder registerValueObject(ValueObjectDefinition valueObjectDefinition) {

        registerType(valueObjectDefinition);
        return this;
    }

    /**
     * Comma separated list of packages scanned by ObjDiff in search of
     * your classes with the {@link TypeName} annotation.
     * <br/><br/>
     * <p>
     * It's <b>important</b> to declare here all of your packages containing classes with {@literal @}TypeName,<br/>
     * because ObjDiff needs <i>live</i> class definitions to properly deserialize Snapshots from {@link ObjDiffRepository}.
     * <br/><br/>
     *
     * <b>For example</b>, consider this class:
     *
     * <pre>
     * {@literal @}Entity
     * {@literal @}TypeName("Person")
     *  class Person {
     *     {@literal @}Id
     *      private int id;
     *      private String name;
     *  }
     * </pre>
     * <p>
     * In the scenario when ObjDiff reads a Snapshot of type named 'Person'
     * before having a chance to map the Person class definition,
     * the 'Person' type will be mapped to generic {@link UnknownType}.
     * <br/><br/>
     * <p>
     * Since 5.8.4, ObjDiff logs <code>WARNING</code> when UnknownType is created
     * because Snapshots with UnknownType can't be properly deserialized from {@link ObjDiffRepository}.
     *
     * @param packagesToScan e.g. "my.company.domain.person, my.company.domain.finance"
     * @since 2.3
     */
    public ObjDiffBuilder withPackagesToScan(String packagesToScan) {
        if (packagesToScan == null || packagesToScan.trim().isEmpty()) {
            return this;
        }

        long start = System.currentTimeMillis();

        List<Class<?>> scan = ReflectionUtil.findClasses(TypeName.class, packagesToScan.replaceAll(" ", "").split(","));
        for (Class<?> c : scan) {
            scanTypeName(c);
        }
        long delta = System.currentTimeMillis() - start;


        return this;
    }

    /**
     * Register your class with &#64;{@link TypeName} annotation
     * in order to use it in all kinds of JQL queries.
     * <br/><br/>
     * <p>
     * You can also use {@link #withPackagesToScan(String)}
     * to scan all your classes.
     * <br/><br/>
     * <p>
     * Technically, this method is the convenient alias for {@link ObjDiff#getTypeMapping(Type)}
     *
     * @since 1.4
     */
    public ObjDiffBuilder scanTypeName(Class userType) {
        classesToScan.add(userType);
        return this;
    }

    /**
     * Registers a simple value type (see {@link ValueType}).
     * <br/><br/>
     * <p>
     * For example, values are: BigDecimal, LocalDateTime.
     * <br/><br/>
     * <p>
     * Use this method if can't use the {@link Value} annotation.
     * <br/><br/>
     * <p>
     * By default, Values are compared using {@link Object#equals(Object)}.
     * You can provide external <code>equals()</code> function
     * by registering a {@link CustomValueComparator}.
     * See {@link #registerValue(Class, CustomValueComparator)}.
     *
     * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#ValueType">http://ObjDiff.org/documentation/domain-configuration/#ValueType</a>
     */
    public ObjDiffBuilder registerValue(Class<?> valueClass) {

        registerType(new ValueDefinition(valueClass));
        return this;
    }

    /**
     * Registers a {@link ValueType} with a custom comparator to be used instead of
     * {@link Object#equals(Object)}.
     * <br/><br/>
     * <p>
     * For example, by default, BigDecimals are Values
     * compared using {@link java.math.BigDecimal#equals(Object)},
     * sadly it isn't the correct mathematical equality:
     *
     * <pre>
     *     new BigDecimal("1.000").equals(new BigDecimal("1.00")) == false
     * </pre>
     * <p>
     * If you want to compare them in the right way &mdash; ignoring trailing zeros &mdash;
     * register this comparator:
     *
     * <pre>
     * ObjDiffBuilder.ObjDiff()
     *     .registerValue(BigDecimal.class, new BigDecimalComparatorWithFixedEquals())
     *     .build();
     * </pre>
     *
     * @param <T> Value Type
     * @see <a href="http://ObjDiff.org/documentation/domain-configuration/#ValueType">http://ObjDiff.org/documentation/domain-configuration/#ValueType</a>
     * @see <a href="https://ObjDiff.org/documentation/diff-configuration/#custom-comparators">https://ObjDiff.org/documentation/diff-configuration/#custom-comparators</a>
     * @see BigDecimalComparatorWithFixedEquals
     * @see CustomBigDecimalComparator
     * @since 3.3
     */
    public <T> ObjDiffBuilder registerValue(Class<T> valueClass, CustomValueComparator<T> customValueComparator) {
        argumentsAreNotNull(valueClass, customValueComparator);
        registerType(new ValueDefinition(valueClass, customValueComparator));
        return this;
    }

    /**
     * Lambda-style variant of {@link #registerValue(Class, CustomValueComparator)}.
     * <br/><br/>
     * <p>
     * For example, you can register the comparator for BigDecimals with fixed equals:
     *
     * <pre>
     * ObjDiff ObjDiff = ObjDiffBuilder.ObjDiff()
     *     .registerValue(BigDecimal.class, (a, b) -> a.compareTo(b) == 0,
     *                                           a -> a.stripTrailingZeros().toString())
     *     .build();
     * </pre>
     *
     * @param <T> Value Type
     * @see #registerValue(Class, CustomValueComparator)
     * @since 5.8
     */
    public <T> ObjDiffBuilder registerValue(Class<T> valueClass,
                                            BiFunction<T, T, Boolean> equalsFunction,
                                            Function<T, String> toStringFunction) {
        Validate.argumentsAreNotNull(valueClass, equalsFunction, toStringFunction);

        return registerValue(valueClass, new CustomValueComparator<T>() {
            @Override
            public boolean equals(T a, T b) {
                return equalsFunction.apply(a, b);
            }

            @Override
            public String toString(T value) {
                return toStringFunction.apply(value);
            }
        });
    }

    /**
     * <b>Deprecated</b>, use {@link #registerValue(Class, CustomValueComparator)}.
     * <p>
     * <br/><br/>
     * <p>
     * Since this comparator is not aligned with {@link Object#hashCode()},
     * it calculates incorrect results when a given Value is used in hashing context
     * (when comparing Sets with Values or Maps with Values as keys).
     *
     * @see CustomValueComparator
     */
    @Deprecated
    public <T> ObjDiffBuilder registerValue(Class<T> valueClass, BiFunction<T, T, Boolean> equalsFunction) {
        Validate.argumentsAreNotNull(valueClass, equalsFunction);

        return registerValue(valueClass, new CustomValueComparator<T>() {
            @Override
            public boolean equals(T a, T b) {
                return equalsFunction.apply(a, b);
            }

            @Override
            public String toString(T value) {
                return value.toString();
            }
        });
    }

    /**
     * <b>Deprecated</b>, use {@link #registerValue(Class, CustomValueComparator)}.
     *
     * @see CustomValueComparator
     * @since 3.7.6
     */
    @Deprecated
    public <T> ObjDiffBuilder registerValueWithCustomToString(Class<T> valueClass, Function<T, String> toStringFunction) {
        Validate.argumentsAreNotNull(valueClass, toStringFunction);
        return registerValue(valueClass, (a, b) -> Objects.equals(a, b), toStringFunction);
    }

    /**
     * Marks given class as ignored by ObjDiff.
     * <br/><br/>
     * <p>
     * Use this method as an alternative to the {@link DiffIgnore} annotation.
     *
     * @see DiffIgnore
     */
    public ObjDiffBuilder registerIgnoredClass(Class<?> ignoredClass) {
        argumentIsNotNull(ignoredClass);
        registerType(new IgnoredTypeDefinition(ignoredClass));
        return this;
    }

    /**
     * A dynamic version of {@link ObjDiffBuilder#registerIgnoredClass(Class)}.
     * <br/>
     * Registers a custom strategy for marking certain classes as ignored.
     * <br/><br/>
     * <p>
     * For example, you can ignore classes by package naming convention:
     *
     * <pre>
     * ObjDiff ObjDiff = ObjDiffBuilder.ObjDiff()
     *         .registerIgnoredClassesStrategy(c -> c.getName().startsWith("com.ignore.me"))
     *         .build();
     * </pre>
     * <p>
     * Use this method as the alternative to the {@link DiffIgnore} annotation
     * or multiple calls of {@link ObjDiffBuilder#registerIgnoredClass(Class)}.
     */
    public ObjDiffBuilder registerIgnoredClassesStrategy(IgnoredClassesStrategy ignoredClassesStrategy) {
        argumentIsNotNull(ignoredClassesStrategy);
        this.ignoredClassesStrategy = ignoredClassesStrategy;
        return this;
    }


    /**
     * The Initial Changes switch, enabled by default since ObjDiff 6.0.
     * <br/><br/>
     * <p>
     * When the switch is enabled, {@link ObjDiff#compare(Object oldVersion, Object currentVersion)}
     * and {@link ObjDiff#findChanges(JqlQuery)}
     * generate additional set of Initial Changes for each
     * property of a NewObject to capture its state.
     * <br/>
     * Internally, ObjDiff generates Initial Changes by comparing a virtual, totally empty object
     * with a real NewObject.
     * <p>
     * <br/><br/>
     * For Primitives and Values
     * an Initial Change is modeled as {@link InitialValueChange} (subtype of {@link ValueChange})
     * with null on left, and a property value on right.
     * <br/>
     * For Collections, there are no specific subtypes to mark Initial Changes.
     * So, for example, an Initial Change for a List is a regular {@link ListChange}
     * with all elements from this list reflected as {@link ValueAdded}.
     * <br/><br/>
     * <p>
     * In ObjDiff Spring Boot starter you can disabled initial Value Changes in `application.yml`:
     *
     * <pre>
     * ObjDiff:
     *   initialChanges: false
     * </pre>
     *
     * @see NewObject
     */
    public ObjDiffBuilder withInitialChanges(boolean initialChanges) {
        configurationBuilder().withInitialChanges(initialChanges);
        return this;
    }

    /**
     * Use {@link #withInitialChanges(boolean)}
     */
    @Deprecated
    public ObjDiffBuilder withNewObjectsSnapshot(boolean newObjectsSnapshot) {
        return this.withInitialChanges(newObjectsSnapshot);
    }


    /**
     * Registers a {@link CustomPropertyComparator} for a given class and maps this class
     * to {@link CustomType}.
     * <br/><br/>
     *
     * <b>
     * Custom Types are not easy to manage, use it as a last resort,<br/>
     * only for corner cases like comparing custom Collection types.</b>
     * <br/><br/>
     * <p>
     * In most cases, it's better to customize the ObjDiff' diff algorithm using
     * much more simpler {@link CustomValueComparator},
     * see {@link #registerValue(Class, CustomValueComparator)}.
     *
     * @param <T> Custom Type
     * @see <a href="https://ObjDiff.org/documentation/diff-configuration/#custom-comparators">https://ObjDiff.org/documentation/diff-configuration/#custom-comparators</a>
     */
    public <T> ObjDiffBuilder registerCustomType(Class<T> customType, CustomPropertyComparator<T, ?> comparator) {
        registerType(new CustomDefinition(customType, comparator));
        bindComponent(comparator, new CustomToNativeAppenderAdapter(comparator, customType));
        return this;
    }

    /**
     * @deprecated Renamed to {@link #registerCustomType(Class, CustomPropertyComparator)}
     */
    @Deprecated
    public <T> ObjDiffBuilder registerCustomComparator(CustomPropertyComparator<T, ?> comparator, Class<T> customType) {
        return registerCustomType(customType, comparator);
    }

    /**
     * Choose between two algorithms for comparing list: ListCompareAlgorithm.SIMPLE
     * or ListCompareAlgorithm.LEVENSHTEIN_DISTANCE.
     * <br/><br/>
     * Generally, we recommend using LEVENSHTEIN_DISTANCE, because it's smarter.
     * However, it can be slow for long lists, so SIMPLE is enabled by default.
     * <br/><br/>
     * <p>
     * Refer to <a href="http://ObjDiff.org/documentation/diff-configuration/#list-algorithms">http://ObjDiff.org/documentation/diff-configuration/#list-algorithms</a>
     * for description of both algorithms
     *
     * @param algorithm ListCompareAlgorithm.SIMPLE is used by default
     */
    public ObjDiffBuilder withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        argumentIsNotNull(algorithm);
        configurationBuilder().withListCompareAlgorithm(algorithm);
        return this;
    }

    /**
     * DateProvider providers current timestamp for {@link Commit#getCommitDate()}.
     * <br/>
     * By default, now() is used.
     * <br/>
     * Overriding default dateProvider probably makes sense only in test environment.
     */
    public ObjDiffBuilder withDateTimeProvider(DateProvider dateProvider) {
        argumentIsNotNull(dateProvider);
        this.dateProvider = dateProvider;
        return this;
    }

    public ObjDiffBuilder withPrettyPrintDateFormats(ObjDiffCoreProperties.PrettyPrintDateFormats prettyPrintDateFormats) {
        configurationBuilder().withPrettyPrintDateFormats(prettyPrintDateFormats);
        return this;
    }


    private void mapRegisteredClasses() {
        TypeMapper typeMapper = typeMapper();
        for (ClientsClassDefinition def : clientsClassDefinitions.values()) {
            typeMapper.registerClientsClass(def);
        }
    }

    private TypeMapper typeMapper() {
        return getContainerComponent(TypeMapper.class);
    }


    private CoreConfigurationBuilder configurationBuilder() {
        return this.coreConfigurationBuilder;
    }

    private JsonConverterBuilder jsonConverterBuilder() {
        return getContainerComponent(JsonConverterBuilder.class);
    }

    private Set<ObjDiffType> bootAddOns() {
        Set<ObjDiffType> additionalTypes = new HashSet<>();


        return additionalTypes;
    }

    /**
     * boots JsonConverter and registers domain aware typeAdapters
     */
    private void bootJsonConverter() {
        JsonConverterBuilder jsonConverterBuilder = jsonConverterBuilder();
        jsonConverterBuilder.prettyPrint(coreConfiguration().isPrettyPrint());

        addModule(new ChangeTypeAdaptersModule(getContainer()));
        addModule(new CommitTypeAdaptersModule(getContainer()));


        jsonConverterBuilder.registerJsonTypeAdapters(getComponents(JsonTypeAdapter.class));
        jsonConverterBuilder.registerNativeGsonDeserializer(Diff.class, new DiffTypeDeserializer());
        JsonConverter jsonConverter = jsonConverterBuilder.build();
        addComponent(jsonConverter);
    }

    private void bootDateTimeProvider() {
        if (dateProvider == null) {
            dateProvider = new DefaultDateProvider();
        }
        addComponent(dateProvider);
    }

    private void bootRepository() {
        CoreConfiguration coreConfiguration = coreConfiguration();
        if (repository == null) {
            repository = new InMemoryRepository();
        }

        repository.setJsonConverter(getContainerComponent(JsonConverter.class));

        if (repository instanceof ConfigurationAware) {
            ((ConfigurationAware) repository).setConfiguration(coreConfiguration);
        }

        bindComponent(ObjDiffRepository.class, repository);

        //ObjDiffExtendedRepository can be created after users calls ObjDiffBuilder.registerObjDiffRepository()
        addComponent(ObjDiffExtendedRepository.class);
    }


    private CoreConfiguration coreConfiguration() {
        return getContainerComponent(CoreConfiguration.class);
    }
}
