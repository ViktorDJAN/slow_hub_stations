package ru.promelectronika.util_stuff;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public final class PropertiesReader {
    private static final Properties properties = new Properties();
    //    private static final String DIRECTORY_PATH = "src/main/resources/configurations/";
//    private static final String DIRECTORY_PATH = "home/root/chargingStation/configurations/";
    private static final String DIRECTORY_PATH = "slow_charging_station/home/root/chargingStation/configurations/";
    private Math math;

    public PropertiesReader(Math math) {
        this.math = math;
    }


    private static void loadProperties() {
        try {
            properties.load(Files.newInputStream(new File(DIRECTORY_PATH + "properties.ini").toPath()));
        } catch (IOException e) {
            System.out.println("Is a file existed? Impossible to load properties from " + DIRECTORY_PATH);
            throw new RuntimeException();
        }
    }

    public static Object getPropertyFromFile(String propertyKey) {
        Object obj = null;
        loadProperties();
        if (!properties.isEmpty()) {
            if (propertyKey.equals("AVAILABLE_HOME_POWER")) {
                obj = Double.parseDouble(properties.getProperty(propertyKey));
            }
        } else {
            obj = (double) 0;
        }
        return obj;
    }
}
