package ro.festival.database;

import java.io.InputStream;
import java.util.Properties;

// Încarcă valorile de configurare (URL, user, parolă) din fișierul config.properties
public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Could not find config.properties in classpath!");
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Error reading config.properties:");
            e.printStackTrace();
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("DB_URL");
    }

    public static String getDbUser() {
        return properties.getProperty("DB_USER");
    }

    public static String getDbPassword() {
        return properties.getProperty("DB_PASSWORD");
    }
}
