package ru.promelectronika.logHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.util.Properties;

public class LogConfiguration {
    private static final Properties properties = new Properties();
//    private static final String DIRECTORY_PATH = "home/root/chargingStation/logger/" ;
    private static final String DIRECTORY_PATH = "slow_charging_station/home/root/chargingStation/logger/" ;


    // LogBack xml file location
    public static String getServerLogbackConfigPath() {
        return DIRECTORY_PATH + "Logback.xml";
    }


    public static void logConfigurator(String logConfigPath) throws JoranException {
        JoranConfigurator configurator = new JoranConfigurator();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        configurator.doConfigure(new File(logConfigPath));
    }
}
