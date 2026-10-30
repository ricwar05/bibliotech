
package pa.bibliotech.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public final class AppConfig {
    private static final Properties PROPS = load();

    private AppConfig() { }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Path.of("application.properties"))) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                "No se encontró application.properties en la carpeta del proyecto", e);
        }
        return props;
    }

    public static String get(String key) {
        String value = PROPS.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la clave en application.properties: " + key);
        }
        return value;
    }
}