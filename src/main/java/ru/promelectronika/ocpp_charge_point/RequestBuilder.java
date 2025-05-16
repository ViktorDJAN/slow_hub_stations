package ru.promelectronika.ocpp_charge_point;

import eu.chargetime.ocpp.model.core_2_0_1.data_types.*;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.*;
import eu.chargetime.ocpp.model.core_2_0_1.messages.*;
import lombok.Getter;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.dto.records.MeterValuesDto;
import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;
import ru.promelectronika.queues.TransactionsQueue;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class RequestBuilder {

    public static BootNotificationRequest buildBootNotificationRequest(BootReasonEnumType reason, String model, String vendorName) {
        ChargingStationType chargingStationType = new ChargingStationType(model, vendorName);
        return new BootNotificationRequest(reason, chargingStationType);
    }

    public static StatusNotificationRequest buildStatusNotificationRequest(ConnectorStatusEnumType connectorStatus, Integer evseId, Integer connectorId) {
        return new StatusNotificationRequest(ZonedDateTime.now(), connectorStatus, evseId, connectorId);
    }

    public static TransactionEventRequest buildTransactionEventRequest(TransactionEventEnumType eventEnumType, TriggerReasonEnumType reasonEnumType
            , ChargingStateEnumType chargingStateEnumType, Integer evseId, Integer connectorId, String transactionId) {

        TransactionType transactionType = new TransactionType(transactionId);
        EVSEType evseType = new EVSEType(evseId);
        evseType.setConnectorId(connectorId);
        transactionType.setChargingState(chargingStateEnumType);

        TransactionEventRequest request = new TransactionEventRequest(eventEnumType, ZonedDateTime.now(), reasonEnumType, TransactionInfo.seqNo++, transactionType);
        request.setEvse(evseType);
        return request;
    }

    public static TransactionEventRequest buildRemoteStartTransactionEventRequest(RequestStartTransactionRequest request) {

        if (!TransactionsQueue.queue.isEmpty()) {
            for (TransactionInfo info : TransactionsQueue.queue) {
                if (info.getRequest().getEvse().getId().equals(request.getEvseId())) {

                    var transactionType = new TransactionType(info.getTransactionId()); // required
                    transactionType.setChargingState(ChargingStateEnumType.Charging);
                    transactionType.setRemoteStartId(request.getRemoteStartId());
                    info.setRemoteId(request.getRemoteStartId());
                    var evseType = new EVSEType(request.getEvseId());

                    var transactionEventRequest = new TransactionEventRequest(TransactionEventEnumType.Updated, ZonedDateTime.now(), TriggerReasonEnumType.RemoteStart,
                            TransactionInfo.seqNo++, transactionType);
                    transactionEventRequest.setIdToken(request.getIdToken());
                    transactionEventRequest.setEvse(evseType);

                    return transactionEventRequest;
                }
            }
        }else{
            ColorTuner.printBlackText("Transactions queue is empty__!!!");
        }
        return null;
    }




    public static TransactionEventRequest buildLocalStopTransactionEventRequest() {
        if (TransactionsQueue.queue.peekFirst() == null) {

            ColorTuner.printBlackText("TransactionsQueue: " + TransactionsQueue.queue.peekFirst());
            return null;
        }
        TransactionInfo info = TransactionsQueue.queue.peekFirst();
        TransactionType transType = new TransactionType(info.getTransactionId());
        transType.setChargingState(ChargingStateEnumType.Idle);
        transType.setStoppedReason(ReasonEnumType.EmergencyStop);
        transType.setRemoteStartId(info.getRemoteId());
        TransactionEventRequest request = new TransactionEventRequest(TransactionEventEnumType.Ended,
                ZonedDateTime.now(), TriggerReasonEnumType.AbnormalCondition, info.getRequest().getSeqNo(), transType);
        return request;
    }

    public static TransactionEventRequest buildRemoteStopTransactionEventRequest(RequestStopTransactionRequest stopTransactionRequest) {
        if (!TransactionsQueue.queue.isEmpty()) {
            for (TransactionInfo info : TransactionsQueue.queue) {
                if (info.getTransactionId().equals(stopTransactionRequest.getTransactionId())) {

                    var transactionType = new TransactionType(info.getTransactionId());
                    transactionType.setChargingState(ChargingStateEnumType.Idle);
                    transactionType.setStoppedReason(ReasonEnumType.Remote);
                    transactionType.setRemoteStartId(info.getRemoteId());
                    var request = new TransactionEventRequest(TransactionEventEnumType.Ended, ZonedDateTime.now(), TriggerReasonEnumType.RemoteStop,
                            info.getRequest().getSeqNo(), transactionType);
                    request.setIdToken(info.getRequest().getIdToken());
                    request.setTransactionInfo(transactionType);
                    return request;
                }
            }
        }
        return null;
    }

    public static TransactionEventRequest buildTransactionEventRequestForMeterValueSending(MeterValuesDto valuesDto) {

        if (!TransactionsQueue.queue.isEmpty()) {
            for (TransactionInfo info : TransactionsQueue.queue) {
                if (info.getRequest().getEvse().getId().equals(valuesDto.evseId()) && info.isStarted()) {

                    TransactionType transactionType = new TransactionType(info.getTransactionId());
                    transactionType.setChargingState(ChargingStateEnumType.Charging);
                    transactionType.setRemoteStartId(info.getRemoteId());

                    SampledValueType sampledCurrentPower = createSampledValueType(valuesDto.currentPower(),
                            MeasurandEnumType.Power_Active_Import, LocationEnumType.Outlet, ReadingContextEnumType.Sample_Periodic);
                    SampledValueType sampledDeliveredPower = createSampledValueType(valuesDto.deliveredPower(),
                            MeasurandEnumType.Energy_Active_Import_Register, LocationEnumType.Outlet, ReadingContextEnumType.Sample_Periodic);

                    List<SampledValueType> listOfSampledValues = List.of(sampledCurrentPower, sampledDeliveredPower);
                    MeterValueType meterValueType = createMeterValueType(listOfSampledValues);

                    var request = new TransactionEventRequest(TransactionEventEnumType.Updated, ZonedDateTime.now(), TriggerReasonEnumType.MeterValuePeriodic,
                            info.getRequest().getSeqNo(), transactionType);

                    request.setIdToken(info.getRequest().getIdToken());
                    request.setMeterValue(List.of(meterValueType));
                    return request;
                }
            }
        }
        return null;

    }


    private static SampledValueType createSampledValueType(double value, MeasurandEnumType measurandEnumType,
                                                           LocationEnumType locationEnumType,
                                                           ReadingContextEnumType readingContextEnumType) {

        SampledValueType sampledValueType = new SampledValueType(value);
        sampledValueType.setMeasurand(measurandEnumType.getMeasurandEnumType());
        sampledValueType.setLocation(locationEnumType);
        sampledValueType.setContext(readingContextEnumType.getValue());
        return sampledValueType;
    }

    private static MeterValueType createMeterValueType(List<SampledValueType> sampledValueTypeList) {
        MeterValueType meterValueType = new MeterValueType(ZonedDateTime.now(), sampledValueTypeList);
        meterValueType.setSampledValue(sampledValueTypeList);
        return meterValueType;

    }


}










