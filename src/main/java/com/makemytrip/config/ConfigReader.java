package com.makemytrip.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.warn("Unable to find config.properties via classloader, attempting direct file path");
                try (FileInputStream fileInput = new FileInputStream("src/main/resources/config.properties")) {
                    properties.load(fileInput);
                }
            } else {
                properties.load(input);
            }
            logger.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load config.properties file: {}", e.getMessage(), e);
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public static String getUrl() {
        return getProperty("url", "https://www.makemytrip.com/flights/");
    }

    public static int getImplicitWait() {
        return Integer.parseInt(getProperty("implicitWait", "10"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperty("explicitWait", "20"));
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public static String getSourceCity() {
        return getProperty("sourceCity", "Delhi");
    }

    public static String getDestinationCity() {
        return getProperty("destinationCity", "Mumbai");
    }

    public static String getGoogleUrl() {
        return getProperty("googleUrl", "https://www.google.com");
    }

    public static String getGoogleSearchQuery() {
        return getProperty("googleSearchQuery", "MakeMyTrip flight status");
    }
}
