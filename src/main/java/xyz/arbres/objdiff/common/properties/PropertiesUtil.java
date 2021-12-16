package xyz.arbres.objdiff.common.properties;

import xyz.arbres.objdiff.common.exception.ObjDiffException;
import xyz.arbres.objdiff.common.validation.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static xyz.arbres.objdiff.common.exception.ObjDiffExceptionCode.*;

/**
 * PropertiesUtil
 *
 * @author carlos
 * @date 2021-12-07
 */
public class PropertiesUtil {

    public static String getStringProperty(Properties properties, String propertyKey) {
        Validate.argumentIsNotNull(properties);
        Validate.argumentIsNotNull(propertyKey);

        if (!properties.containsKey(propertyKey)) {
            throw new ObjDiffException(UNDEFINED_PROPERTY, propertyKey);
        }

        return properties.getProperty(propertyKey);
    }

    public static boolean getBooleanProperty(Properties properties, String propertyKey) {
        String val = getStringProperty(properties, propertyKey);
        return Boolean.parseBoolean(val);
    }

    public static <T extends Enum<T>> T getEnumProperty(Properties properties, String propertyKey, Class<T> enumType) {
        String enumName = getStringProperty(properties, propertyKey);
        Validate.argumentIsNotNull(enumType);

        try {
            return Enum.valueOf(enumType, enumName);
        } catch (IllegalArgumentException e) {
            throw new ObjDiffException(MALFORMED_PROPERTY, enumName, propertyKey);
        }
    }

    public static Properties getProperties(String classpathName) {
        Properties properties = new Properties();
        loadProperties(classpathName, properties);
        return properties;
    }

    public static void loadProperties(String classpathName, Properties toProps) {
        Validate.argumentIsNotNull(classpathName);
        Validate.argumentIsNotNull(toProps);

        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(classpathName);

        if (inputStream == null) {
            throw new ObjDiffException(CLASSPATH_RESOURCE_NOT_FOUND, classpathName);
        }

        try {
            toProps.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


