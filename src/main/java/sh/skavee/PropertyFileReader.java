package sh.skavee;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReader {

    private PropertyFileReader() {
    }

    public static Properties propertyReader(String propertyFilePath) {

        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader(propertyFilePath);
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String getPropertyPath(String entity) {
        return "src/main/resources/environments/qa/" + entity + "/" + entity + ".properties";
    }
}
