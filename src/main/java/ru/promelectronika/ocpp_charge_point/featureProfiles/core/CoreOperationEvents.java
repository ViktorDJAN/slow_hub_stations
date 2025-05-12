package ru.promelectronika.ocpp_charge_point.featureProfiles.core;

import eu.chargetime.ocpp.model.RequestWithId;
import eu.chargetime.ocpp.model.core_2_0_1.messages.*;

/***
 * Commands ChargeStation(client) sends to CSMS
 */
public  abstract interface CoreOperationEvents {

    public abstract void sendAuthorizeRequest(RequestWithId request);

    public abstract void sendDataTransferRequest(RequestWithId request);

    public abstract void sendNotifyReportRequest(NotifyReportRequest request);

    public abstract void sendBootNotificationRequest(BootNotificationRequest request);// charge point sends request to Central System with info about Configuration(version,vendor...)

    public abstract void sendHeartbeatRequest(HeartbeatRequest request); //

    public abstract void sendMeterValueRequest(MeterValuesRequest request);

    public abstract void sendStatusNotificationRequest(StatusNotificationRequest request);

    public abstract void sendTransactionEventRequest(TransactionEventRequest request);

    public abstract void sendStopTransactionRequest(TransactionEventRequest request);

}
