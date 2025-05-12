package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core.GetConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.KeyValueType;
import ru.promelectronika.ocpp_charge_point.configuration.Accessibility;
import ru.promelectronika.ocpp_charge_point.configuration.ConfigurationObject;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigsWorker;
import ru.promelectronika.logHandler.LogHandler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/** CENTRAL_SYSTEM REQUESTS a CHARGE POINT FOR DIAGNOSTIC INFO
 */
//  To retrieve the value of configuration settings, the Central System SHALL send a GetConfigurationRequestHandler.req
public class GetConfigurationRequestHandler {

    private final GetConfigurationRequest request;

    public GetConfigurationRequestHandler(GetConfigurationRequest request) {
        this.request = request;
    }

// If the list of keys in the request of Central_system is empty or missing. Charge Point SHALL return
// a list of all configuration settings
    public GetConfigurationConfirmation getResponse(){
        try {
            String[] keys = request.getKey();
            if (keys == null || keys.length == 0) {
                return getAllConfigurations();
            } else {
                return getKnownAndUnknownKeys(keys);
            }
        } catch (IOException e) {
            LogHandler.logOcpp(e);
            return new GetConfigurationConfirmation();
        }
    }
    // Gets all configurations from JSON file
    private GetConfigurationConfirmation getAllConfigurations() throws IOException {
        // KeyValueType = {"key", "readonly", "value"}
        // ConfigurationObject = {"accessibility","type","unit","value"}
        List<KeyValueType> keyValues = new ArrayList<>();
        Map<String, ConfigurationObject> configurations = OcppConfigsWorker.getConfigObjectsMapFromJson();
        configurations.forEach(
                (key, object) -> {
                    KeyValueType keyValue = new KeyValueType(key, Accessibility.R.equals(object.getAccessibility()));
                    if (object.getValue() != null) {
                        keyValue.setValue(object.getValue());
                    }
                    keyValues.add(keyValue);
                }
        );
        // GetConfigurationConfirmation = {"configurationKey []", "unknownKey []"})
        GetConfigurationConfirmation response = new GetConfigurationConfirmation();
        response.setConfigurationKey(keyValues.toArray(KeyValueType[]::new));
        return response;
    }

    // Charge Point SHALL return a list
    // of recognized keys and their corresponding values and read-only state. Unrecognized keys SHALL be
    // placed in the response PDU as part of the optional unknown key list element

    private GetConfigurationConfirmation getKnownAndUnknownKeys(String[] keys) {
        //KeyValueType{key=RUN, readonly=true, value=NOW_AND}
        List<KeyValueType> keyValues = new ArrayList<>();
        List<String> unknownKeys = new ArrayList<>();
        for (String key : keys) {
            try {
                ConfigurationObject configuration = OcppConfigsWorker.getConfigObjectFromMap(key);
                if (configuration != null) {
                    KeyValueType keyValue = new KeyValueType(key, Accessibility.R.equals(configuration.getAccessibility()));
                    if (configuration.getValue() != null) {
                        keyValue.setValue(configuration.getValue());
                    }
                    keyValues.add(keyValue);
                }
                else {
                    unknownKeys.add(key);
                }
            } catch (IOException e) {
                unknownKeys.add(key);
            }
        }
        // GetConfigurationConfirmation = {"configurationKey []", "unknownKey []"})
        GetConfigurationConfirmation response = new GetConfigurationConfirmation();
        response.setConfigurationKey(keyValues.toArray(KeyValueType[]::new));
        if (!unknownKeys.isEmpty()) {
            response.setUnknownKey(unknownKeys.toArray(String[]::new));
        }
        return response;
    }
}
