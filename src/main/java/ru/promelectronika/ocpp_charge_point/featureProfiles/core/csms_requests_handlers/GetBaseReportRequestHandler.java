package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core_2_0_1.data_types.StatusInfoType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.GenericDeviceModelStatusEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.messages.GetBaseReportRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.GetBaseReportResponse;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.queues.RequestIdQueue;


public class GetBaseReportRequestHandler {
    private final GetBaseReportRequest request;
    private final ChargePointOcpp chargePoint;

    public GetBaseReportRequestHandler(GetBaseReportRequest request, ChargePointOcpp chargePoint) {
        this.request = request;
        this.chargePoint = chargePoint;
    }

    public GetBaseReportResponse formResponse() {
        GetBaseReportResponse response;
        if (!request.validate()) {
            response = new GetBaseReportResponse(GenericDeviceModelStatusEnumType.Rejected);
            response.setStatusInfo(new StatusInfoType("Request is not valid"));
            return response;
        }
        response = new GetBaseReportResponse(GenericDeviceModelStatusEnumType.Accepted);
        response.setStatusInfo(new StatusInfoType("Request is accepted"));
        RequestIdQueue.deque.addFirst(request.getRequestId());
        return response;
    }


}
