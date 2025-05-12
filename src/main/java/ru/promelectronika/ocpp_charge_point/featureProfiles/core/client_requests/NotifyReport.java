package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core_2_0_1.data_types.ComponentType;
import eu.chargetime.ocpp.model.core_2_0_1.data_types.ReportDataType;
import eu.chargetime.ocpp.model.core_2_0_1.data_types.VariableAttributeType;
import eu.chargetime.ocpp.model.core_2_0_1.data_types.VariableType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.AttributeEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.MutabilityEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.messages.NotifyReportRequest;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;


import java.util.ArrayList;
import java.util.List;

public class NotifyReport extends OcppOperation {

    public final NotifyReportRequest request;

    public NotifyReport(NotifyReportRequest request) {
        this.request = request;
    }

    public void sendRequest() {
        // check if a request is composed not properly
        if (!request.validate()) {
            ColorTuner.blackBackgroundRedText("Request is not valid");
        }
        List<ReportDataType> list = new ArrayList<>();
        ReportDataType reportData = createReportData();
        list.add(reportData);
        request.setReportData(list);

        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleNotifyReportResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
            throw new RuntimeException(e);
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "NotifyReportRequest REQ: {} " + request);


    }


    private void handleNotifyReportResponse(Confirmation confirmation, Throwable throwable) {
        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "NotifyReport RESP: {} "+ confirmation);
        }
    }


    public ReportDataType createReportData() {


        VariableAttributeType variableAttribute = new VariableAttributeType();
        variableAttribute.setType(AttributeEnumType.Actual);
        variableAttribute.setValue("AB1");
        variableAttribute.setMutability(MutabilityEnumType.ReadOnly);
        variableAttribute.setPersistent(true);
        variableAttribute.setConstant(true);
        ReportDataType reportDataType = new ReportDataType(new ComponentType("EVSE 1"), new VariableType("VehicleId"),
                variableAttribute);

        reportDataType.setVariableAttribute(variableAttribute);

        return reportDataType;
    }

}
