package ru.promelectronika.logHandler;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ru.promelectronika.enums.LoggerType;


import java.io.PrintWriter;
import java.io.StringWriter;

public class LogHandler {
    private static final StringWriter sw = new StringWriter();
    private static final PrintWriter pw = new PrintWriter(sw);
    public static Logger loggerMain = (Logger) LoggerFactory.getLogger(LoggerType.MAIN_LOGGER.name());
    public static Logger loggerServer = (Logger) LoggerFactory.getLogger(LoggerType.RPC_SERVER_LOGGER.name());
    public static Logger loggerEnergyMeterProcess = (Logger) LoggerFactory.getLogger(LoggerType.ENERGY_METER_LOGGER.name());
    public static Logger loggerMode3Sending =       (Logger) LoggerFactory.getLogger(LoggerType.MODE3_LOGGER.name());
    public static Logger loggerOcpp = (Logger) LoggerFactory.getLogger(LoggerType.OCPP_LOGGER.getName());


    public static void logThrowableMain(Throwable e) {
        e.printStackTrace(pw);
        loggerMain.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }

    public static void logOcpp(Throwable e) {
        e.printStackTrace(pw);
        loggerOcpp.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }

    public static void logThrowableMode3(Throwable e) {
        e.printStackTrace(pw);
        loggerMode3Sending.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }

    public static void logThrowableServer(Throwable e) {
        e.printStackTrace(pw);
        loggerServer.error(sw.toString());
        sw.getBuffer().setLength(0);
        pw.flush();
    }

}
