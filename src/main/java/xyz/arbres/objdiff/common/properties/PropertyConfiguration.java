package xyz.arbres.objdiff.common.properties;



import java.util.Properties;

/**
 * PropertyConfiguration
 *
 * @author carlos
 * @date 2021-12-07
 */
public class PropertyConfiguration {



    /**
     * raw String properties bag, loaded from configuration file
     */
    private final Properties properties;

    /**
     * loads a properties file from classpath
     * @param classpathName classpath resource name, ex. "resources/config.properties"
     */
    public PropertyConfiguration(String classpathName) {
        properties = PropertiesUtil.getProperties(classpathName);
    }

    /**
     * assembles mandatory enum property from {@link #properties} bag

     */
    public <T extends Enum<T>> T getEnumProperty(String propertyKey, Class<T> enumType) {
        return PropertiesUtil.getEnumProperty(properties, propertyKey, enumType);
    }

    public boolean contains(String propertyKey) {
        return properties.containsKey(propertyKey);
    }

    /**
     * gets mandatory String property from {@link #properties} bag

     */
    public String getStringProperty(String propertyKey) {
        return PropertiesUtil.getStringProperty(properties, propertyKey);
    }

    /**

     */
    public boolean getBooleanProperty(String propertyKey) {
        return PropertiesUtil.getBooleanProperty(properties, propertyKey);
    }
}
