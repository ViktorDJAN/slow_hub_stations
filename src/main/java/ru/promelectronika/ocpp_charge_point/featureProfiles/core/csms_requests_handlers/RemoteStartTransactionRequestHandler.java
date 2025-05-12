package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core_2_0_1.data_types.StatusInfoType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.*;
import eu.chargetime.ocpp.model.core_2_0_1.messages.RequestStartTransactionRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.RequestStartTransactionResponse;
import eu.chargetime.ocpp.model.core_2_0_1.messages.TransactionEventRequest;
import ru.promelectronika.dto.records.EvseDto;
import ru.promelectronika.dto.records.ProxyCommandDto;
import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;
import ru.promelectronika.queues.TransactionsQueue;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.HandlerEnumType;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.enums.ProxyCommandsEnumType;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;
import ru.promelectronika.ocpp_charge_point.RequestBuilder;


/**
 * CENTRAL_SYSTEM REQUESTS a CHARGE POINT TO START A TRANSACTION
 */
public class RemoteStartTransactionRequestHandler {

    private final RequestStartTransactionRequest request;
    private final ChargePointOcpp chargePoint;
    private final OcppProxyHandler proxyHandler;


    public RemoteStartTransactionRequestHandler(RequestStartTransactionRequest request, ChargePointOcpp chargePoint) {
        this.request = request;
        this.chargePoint = chargePoint;
        this.proxyHandler = new OcppProxyHandler(chargePoint);
    }

    public RequestStartTransactionResponse getResponse() {
        if (request.getEvseId() != 0 && request.getIdToken().getType() == IdTokenEnumType.Central
                && !request.getIdToken().getIdToken().isEmpty() && request.getRemoteStartId() != 0) {

            // SENDS RESPONSE BACK TO CSMS OF THE START ENERGY SUPPLYING
            TransactionEventRequest eventRequest = RequestBuilder.buildRemoteStartTransactionEventRequest(request);
            chargePoint.sendTransactionEventRequest(eventRequest);

            // SEND COMMAND INTO PROXY, TO NOTIFY A PHYSICAL STATION
            var dto = new EvseDto(request.getEvseId(), null, null, null);
            proxyHandler.sendCommand(new ProxyCommandDto(HandlerEnumType.STATION, ProxyCommandsEnumType.START_POWER_SUPPLY.getValue(), dto));

            // TRANSACTION IS STARTED
            for(TransactionInfo trnsInfo: TransactionsQueue.queue){
                if(trnsInfo.getRequest().getEvse().getId().equals(request.getEvseId())){
                    trnsInfo.setStarted(true);
//                    trnsInfo.setRemoteId(request.getRemoteStartId());
                }
            }




            // RESPONSE FORMING
            var response = new RequestStartTransactionResponse(RequestStartStopStatusEnumType.Accepted);
            StatusInfoType statusInfo = new StatusInfoType(TriggerReasonEnumType.RemoteStart.toString());
            statusInfo.setAdditionalInfo("OK: " + request.getIdToken().getIdToken());
            response.setStatusInfo(statusInfo);

            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "RECEIVED_FROM_CSMS RequestStartTransactionRequest REQ: {} " + request);
            return response;
        } else {
            return new RequestStartTransactionResponse(RequestStartStopStatusEnumType.Rejected);

        }
    }
}

