package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hojha on 23/08/17.
 */
public class ConfigReader {

    private static Properties prop = new Properties();

    static {
        try {
            InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return String.valueOf(prop.get(key));
    }

}
