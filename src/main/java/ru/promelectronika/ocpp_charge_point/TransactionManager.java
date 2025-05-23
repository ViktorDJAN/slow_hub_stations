package ru.promelectronika.ocpp_charge_point;


import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class TransactionManager {

    public static synchronized Properties getTransactionProperties(int transactionId) throws IOException {
        try (FileInputStream connectorFile = new FileInputStream(OcppConfigs.TRANSACTIONS_DIRECTORY + transactionId)) {
            Properties connectorProperties = new Properties();
            connectorProperties.load(connectorFile);
            return connectorProperties;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    public static synchronized void setTransactionProperties(Properties properties, int transactionId) {
        try (FileOutputStream connectorFile = new FileOutputStream(OcppConfigs.TRANSACTIONS_DIRECTORY + transactionId)) {
            properties.store(connectorFile, null);
        } catch (IOException e) {
            LogHandler.logOcpp(e);
        }
    }
}
