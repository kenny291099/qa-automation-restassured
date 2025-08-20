package com.restfulbooker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class to manage application properties
 */
public class Configuration {
    private static final Properties properties = new Properties();
    private static Configuration instance;
    
    private Configuration() {
        loadProperties();
    }
    
    public static Configuration getInstance() {
        if (instance == null) {
            synchronized (Configuration.class) {
                if (instance == null) {
                    instance = new Configuration();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties", e);
        }
    }
    
    public String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    public String getBaseUrl() {
        return getProperty("base.url", "https://restful-booker.herokuapp.com");
    }
    
    public int getRequestTimeout() {
        return Integer.parseInt(getProperty("request.timeout", "30000"));
    }
    
    public boolean isLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("logging.enabled", "true"));
    }
}
