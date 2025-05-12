package ru.promelectronika.ocpp_charge_point;

import lombok.Synchronized;
import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class ReservationManager {
    @Synchronized
    public static Properties getReservationProperties(int reservationId) throws IOException {
        try (FileInputStream reservationFile = new FileInputStream(OcppConfigs.RESERVATIONS_DIRECTORY + reservationId)) {
            Properties reservationProperties = new Properties();
            reservationProperties.load(reservationFile);
            return reservationProperties;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Synchronized
    public static void setReservationProperties(Properties properties, int reservationId) {
        try (FileOutputStream reservationFile = new FileOutputStream(OcppConfigs.RESERVATIONS_DIRECTORY + reservationId)) {
            properties.store(reservationFile, null);
        } catch (IOException e) {
            LogHandler.logOcpp(e);
        }
    }
}
