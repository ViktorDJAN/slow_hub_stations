package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;

/**  IF CENTRAL_SYSTEM NEEDS TO SEND INFO TO A CHARGE POINT (for function not supported by OCPP)
 */
public class DataTransferConfRequestHandler {

    private final DataTransferRequest request;

    public DataTransferConfRequestHandler(DataTransferRequest request) {
        this.request = request;
    }

    public DataTransferConfirmation getResponse() {
        return new DataTransferConfirmation(DataTransferStatus.Rejected);
    }
}
