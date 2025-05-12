package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core.ClearCacheConfirmation;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.ClearCacheStatus;

/** CLEARING AUTHORIZATION CACHE OF CHARGE POINT IF CENTRAL_SYSTEM NEEDS IT
 */


public class ClearCacheRequestHandler {
    private final ClearCacheRequest request;

    public ClearCacheRequestHandler(ClearCacheRequest request) {
        this.request = request;
    }

    public ClearCacheConfirmation getResponse() {
        return new ClearCacheConfirmation(ClearCacheStatus.Rejected);
    }
}
