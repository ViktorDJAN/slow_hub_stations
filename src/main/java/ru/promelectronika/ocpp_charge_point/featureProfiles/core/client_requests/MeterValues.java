package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core_2_0_1.messages.MeterValuesRequest;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;

public class MeterValues extends OcppOperation {

    private final MeterValuesRequest request;

    public MeterValues(MeterValuesRequest request) {
        this.request = request;
    }



    public void sendRequest()  {
        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleMeterValuesResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "MeterValues REQ: {} "+ request);
    }

    private void handleMeterValuesResponse(Confirmation confirmation, Throwable throwable) {
        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "MeterValues RESP: {} "+ confirmation);
        }
    }
}
