package ru.promelectronika.ocpp_charge_point.featureProfiles.remoteTrigger;


import eu.chargetime.ocpp.feature.profile.ClientRemoteTriggerEventHandler;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;

import ru.promelectronika.logHandler.LogHandler;



public class RemoteTriggerEventHandler implements ClientRemoteTriggerEventHandler {

    private TriggerConfirmationEvents triggerConfirmationEvents;

    public RemoteTriggerEventHandler(TriggerConfirmationEvents triggerConfirmationEvents) {
        this.triggerConfirmationEvents = triggerConfirmationEvents;
    }

    public TriggerConfirmationEvents getTriggerConfirmationEvents() {
        return triggerConfirmationEvents;
    }

    public void setTriggerConfirmationEvents(TriggerConfirmationEvents triggerConfirmationEvents) {
        this.triggerConfirmationEvents = triggerConfirmationEvents;
    }

    @Override
    public TriggerMessageConfirmation handleTriggerMessageRequest(TriggerMessageRequest request) {
        LogHandler.loggerOcpp.info("TriggerMessage REQ : {}", request);
        TriggerMessageConfirmation response = triggerConfirmationEvents.triggerMessage(request);
        LogHandler.loggerOcpp.info("TriggerMessage SEND: {}", response);
        return response;
    }


}
