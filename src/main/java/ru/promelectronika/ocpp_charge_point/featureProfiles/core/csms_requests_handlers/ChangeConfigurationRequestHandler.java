package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core.ChangeConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ConfigurationStatus;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigsWorker;


/**
 * CENTRAL_SYSTEM REQUESTS TO CHANGE CONFIGURATION PARAM OF CHARGE POINT
 */
public class ChangeConfigurationRequestHandler {
    private final ChangeConfigurationRequest request;

    public ChangeConfigurationRequestHandler(ChangeConfigurationRequest request) {
        this.request = request;
    }

    public ChangeConfigurationConfirmation getResponse() {
        ConfigurationStatus status = OcppConfigsWorker.setConfigurationStatus(request.getKey(), request.getValue());
        return new ChangeConfigurationConfirmation(status);
    }
}
