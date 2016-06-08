package com.piotrek.transactions.util;

import com.piotrek.transactions.exception.SystemConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads immediately system property file once in memory.
 * Allows to retrieve what role certain node have and IP addresses of other participants.
 *
 * @author Piotrek
 */
public class SystemProperties {
    //public static final String ROLE = "role";
    public static final String DEFAULT_PORT = "port";
    public static final String COHORT_IP = "cohort_ip";
    //public static final String IP = "ip";
    //public static final String COORDINATOR_IP = "coordinator_ip";

    private static final String PROPERTIES_PATH = "system.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertiesFile = classLoader.getResourceAsStream(PROPERTIES_PATH);
        if (propertiesFile == null) {
            throw new SystemConfigurationException("Properties file '" + PROPERTIES_PATH + "' is missing in classpath.");
        }

        try {
            PROPERTIES.load(propertiesFile);
        } catch (IOException e) {
            throw new SystemConfigurationException("Cannot load properties file '" + PROPERTIES_PATH + "'.", e);
        }
    }

    /**
     * Returns the SystemProperties instance specific property value associated with the given key with
     * the option to indicate whether the property is mandatory or not.
     *
     * @param key       The key to be associated with a SystemProperties instance specific value.
     * @param mandatory Sets whether the returned property value should not be null nor empty.
     * @return The SystemProperties instance specific property value associated with the given key.
     */
    public static String getProperty(String key, boolean mandatory) {
        String property = PROPERTIES.getProperty(key);

        if (property == null || property.trim().length() == 0) {
            if (mandatory) {
                throw new SystemConfigurationException("Required property '" + key + "'"
                        + " is missing in properties file '" + PROPERTIES_PATH + "'.");
            } else {
                // Make empty value null. Empty Strings are evil.
                property = null;
            }
        }

        return property;
    }
}
