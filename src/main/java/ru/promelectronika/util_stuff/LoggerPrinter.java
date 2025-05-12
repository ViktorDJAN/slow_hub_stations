package ru.promelectronika.util_stuff;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.logHandler.LogConfiguration;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * This class allows you to log data and to print them on a console simultaneously
 */
public class LoggerPrinter {

    public static void turnOnLogConfigurator(boolean isOn) throws JoranException {
        if (isOn) {
            LogConfiguration.logConfigurator(LogConfiguration.getServerLogbackConfigPath());
        }else{
            ColorTuner.redBackgroundBlackText("[LoggerPrinter class]: Logging and colorful output on console is turned off");
        }

    }

    public static void logAndPrint(ColorKind colorKind, LoggerType loggerType, String text)  {
        Logger newLogger = (Logger) LoggerFactory.getLogger(loggerType.name());
        newLogger.info(text);
        Method[] declaredMethods = ColorTuner.class.getDeclaredMethods();
        Stream.of(declaredMethods)
                .filter(method -> method.getName().equals(colorKind.getMethodName()))
                .findFirst()
                .ifPresentOrElse(method -> {
                    try {
                        method.invoke(ColorTuner.class, text);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> {
                    System.out.println("[LoggerPrinter class] : Method not found");
                });
    }
}




