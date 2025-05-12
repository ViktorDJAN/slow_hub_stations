package ru.promelectronika.ocpp_charge_point.featureProfiles.remoteTrigger;

import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;

public interface TriggerConfirmationEvents {
    TriggerMessageConfirmation triggerMessage(TriggerMessageRequest request);
}
