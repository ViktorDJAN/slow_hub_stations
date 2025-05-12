package ru.promelectronika.ocpp_charge_point.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.chargetime.ocpp.model.core.ConfigurationStatus;
import ru.promelectronika.logHandler.LogHandler;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static eu.chargetime.ocpp.model.core.ConfigurationStatus.*;


public class OcppConfigsWorker {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    public static ConfigurationStatus setConfigurationStatus(String key, String value) {
        try {
            Map<String, ConfigurationObject> configurations = getConfigObjectsMapFromJson();
            ConfigurationObject configObject = configurations.get(key);
            if (configObject == null) {
                return NotSupported;
            }
            if (configObject.getAccessibility() == Accessibility.R) {
                return Rejected;
            }
            configObject.setValue(value);
            writeConfigObjectsMapToJson(configurations);
            return RebootRequired;
        } catch (IOException e) {
            return Rejected;
        }
    }

    public static ConfigurationObject getConfigObjectFromMap(String key) throws IOException {
        Map<String, ConfigurationObject> configurations = getConfigObjectsMapFromJson();
        return configurations.get(key);
    }

    public static Map<String, ConfigurationObject> getConfigObjectsMapFromJson() throws IOException {
        try (FileInputStream configFile = new FileInputStream(OcppConfigs.OCPP_CONFIGURATIONS)) {
            return JSON_MAPPER.readValue(configFile, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private static void writeConfigObjectsMapToJson(Map<String, ConfigurationObject> configurations) {
        try (FileOutputStream configFile = new FileOutputStream(OcppConfigs.OCPP_CONFIGURATIONS)) {
            JSON_MAPPER.writeValue(configFile, configurations);
        } catch (IOException e) {
            LogHandler.logOcpp(e);
        }
    }
}
