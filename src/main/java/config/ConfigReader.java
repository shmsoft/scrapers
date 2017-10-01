package config;

import java.io.File;
import java.io.FileInputStream;
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
            File file = new File(System.getProperty("user.home") + "/config.properties");
            if (file.exists()) {
                System.out.println("File exists on root");
                prop.load(new FileInputStream(file));
            } else {
                System.out.println("File does not exists on root");
                InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");
                prop.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return String.valueOf(prop.get(key));
    }

}
