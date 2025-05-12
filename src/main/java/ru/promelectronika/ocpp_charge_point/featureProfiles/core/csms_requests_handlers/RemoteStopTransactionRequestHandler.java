package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core_2_0_1.enumerations.RequestStartStopStatusEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.messages.RequestStopTransactionRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.RequestStopTransactionResponse;
import eu.chargetime.ocpp.model.core_2_0_1.messages.TransactionEventRequest;
import ru.promelectronika.dto.records.EvseDto;
import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.dto.records.ProxyCommandDto;
import ru.promelectronika.enums.HandlerEnumType;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.enums.ProxyCommandsEnumType;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;
import ru.promelectronika.ocpp_charge_point.RequestBuilder;
import ru.promelectronika.queues.TransactionsQueue;


public class RemoteStopTransactionRequestHandler {

    private final RequestStopTransactionRequest request;
    private final ChargePointOcpp chargePoint;
    private OcppProxyHandler proxyHandler;


    public RemoteStopTransactionRequestHandler(RequestStopTransactionRequest request, ChargePointOcpp chargePoint) {
        this.request = request;
        this.chargePoint = chargePoint;
        this.proxyHandler = new OcppProxyHandler(chargePoint);
    }

    public RequestStopTransactionResponse getResponse() {

        if (TransactionsQueue.queue.peekFirst() != null) {
            ColorTuner.blackBackgroundRedText("REMOTE_STOP_GET_TRANSACTION_RESPONSE: CAN BE SENT");
//            if (TransactionsQueue.queue.peekFirst().getTransactionId().equals(request.getTransactionId())) {
//                proxyHandler.sendCommand(new ProxyCommandDto(HandlerEnumType.STATION, ProxyCommandsEnumType.STOP_POWER_SUPPLY.getValue(), null));
//            }
            TransactionEventRequest stopRequest = RequestBuilder.buildRemoteStopTransactionEventRequest(request);

            chargePoint.sendTransactionEventRequest(stopRequest);
            ColorTuner.printRedText("STOP_REQUEST: " + stopRequest);

            //REMOVING TRANSACTION
            for (TransactionInfo info : TransactionsQueue.queue) {
                if (info.getTransactionId().equals(request.getTransactionId()) && info.isStarted()) {
                    EvseDto dto = new EvseDto(info.getRequest().getEvse().getId(), null, 1, null);
                    ColorTuner.printRedText("EVSE_DTO___" + dto);
                    proxyHandler.sendCommand(new ProxyCommandDto(HandlerEnumType.STATION, ProxyCommandsEnumType.STOP_POWER_SUPPLY.getValue(), dto));
//                    boolean remove = TransactionsQueue.queue.remove(info);
//                    ColorTuner.printBlackText("REMOVED TRANSACTION : " + remove);
                }
            }
//            TransactionsQueue.queue.removeFirst();// removing

            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "RECEIVED_FROM_CSMS RequestStopTransactionRequest REQ: {} " + request);
            return new RequestStopTransactionResponse(RequestStartStopStatusEnumType.Accepted);
        } else {
            ColorTuner.blackBackgroundRedText("REMOTE_STOP_GET_TRANSACTION_RESPONSE: CAN ______NOT_____ BE SENT");
            return new RequestStopTransactionResponse(RequestStartStopStatusEnumType.Rejected);
        }
    }

}
