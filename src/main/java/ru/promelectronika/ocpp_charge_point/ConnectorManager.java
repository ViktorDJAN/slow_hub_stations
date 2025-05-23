package ru.promelectronika.ocpp_charge_point;


import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class ConnectorManager {


    public static synchronized Properties getConnectorProperties(int connectorId) throws IOException {
        try (FileInputStream connectorFile = new FileInputStream(OcppConfigs.CONNECTORS_DIRECTORY + connectorId)) {
            Properties connectorProperties = new Properties();
             connectorProperties.load(connectorFile);
             return connectorProperties;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    public static synchronized void setConnectorProperties(Properties properties, int connectorId) {
        try (FileOutputStream connectorFile = new FileOutputStream(OcppConfigs.CONNECTORS_DIRECTORY + connectorId)) {
            properties.store(connectorFile, null); //
        } catch (IOException e) {
            LogHandler.logOcpp(e);
        }
    }
}
