package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core_2_0_1.messages.StatusNotificationRequest;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * kashtanov 2.0.1
 */
public class StatusNotification extends OcppOperation {
    private final StatusNotificationRequest request;

    public StatusNotification(StatusNotificationRequest request) {
        this.request = request;
    }

    public void sendRequest() {
        request.setEvseId(request.getEvseId());
        request.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
        try {
            getChargePointOcpp().getClient().send(request).whenComplete(this::handleStatusNotificationResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
            throw new RuntimeException(e);
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "StatusNotification REQ: {}"+ request);
    }

    private void handleStatusNotificationResponse(Confirmation confirmation, Throwable throwable) {
        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "StatusNotification RESP: {}"+ confirmation);
        }
    }
}
