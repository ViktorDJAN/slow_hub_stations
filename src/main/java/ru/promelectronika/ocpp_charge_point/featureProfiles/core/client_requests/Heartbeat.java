package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core_2_0_1.messages.HeartbeatRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.HeartbeatResponse;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;


public class Heartbeat extends OcppOperation {

    private final HeartbeatRequest request;

    public Heartbeat(HeartbeatRequest request) {
        this.request = request;
    }


    public void sendRequest() {

        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleHeartbeatResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
            throw new RuntimeException(e);
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "Heartbeat REQ: {}" + request);
    }

    private void handleHeartbeatResponse(Confirmation confirmation, Throwable throwable) {
        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            HeartbeatResponse response = (HeartbeatResponse) confirmation;
            String currentTime = response  // transforms it to ==>  2025-03-07 08:48:56.784
                    .getCurrentTime().toString()
                    .replace("T", " ").replace("Z", "");
            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "Heartbeat RESP: {}" + response);
        }
    }
}
