package ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers;

import eu.chargetime.ocpp.model.core_2_0_1.enumerations.UnlockStatusEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.messages.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.UnlockConnectorResponse;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.queues.TransactionsQueue;
import ru.promelectronika.dto.records.ProxyCommandDto;
import ru.promelectronika.enums.HandlerEnumType;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.enums.ProxyCommandsEnumType;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;


import java.util.Objects;
import java.util.function.BooleanSupplier;


public class UnlockConnectorRequestHandler {

    private final UnlockConnectorRequest request;
    private final ChargePointOcpp chargePoint;
    private final OcppProxyHandler proxyHandler;

    public UnlockConnectorRequestHandler(UnlockConnectorRequest request, ChargePointOcpp chargePoint) {
        this.request = request;
        this.chargePoint = chargePoint;
        this.proxyHandler = new OcppProxyHandler(chargePoint);
    }

    public UnlockConnectorResponse getResponse() {
        TransactionInfo transactionInfo = (!TransactionsQueue.queue.isEmpty() ? TransactionsQueue.queue.peekFirst() : (null));
        BooleanSupplier isEvseIdMatched = () -> Objects.requireNonNull(transactionInfo).getRequest().getEvse().getId().equals(request.getEvseId());
        BooleanSupplier isConnectorIdMatched = () -> Objects.requireNonNull(transactionInfo).getRequest().getEvse().getConnectorId().equals(request.getConnectorId());
        if (isEvseIdMatched.getAsBoolean() && isConnectorIdMatched.getAsBoolean()) {
            TransactionsQueue.queue.removeFirst();
            proxyHandler.sendCommand(new ProxyCommandDto(HandlerEnumType.STATION, ProxyCommandsEnumType.STOP_POWER_SUPPLY.getValue(), null));
            System.out.println(UnlockStatusEnumType.Unlocked.name() + " " + UnlockStatusEnumType.Unlocked);
            return new UnlockConnectorResponse(UnlockStatusEnumType.Unlocked);
        } else if (isEvseIdMatched.getAsBoolean() && !isConnectorIdMatched.getAsBoolean()) {
            System.out.println(UnlockStatusEnumType.UnknownConnector.name() + " " + UnlockStatusEnumType.UnknownConnector);
            return new UnlockConnectorResponse(UnlockStatusEnumType.UnknownConnector);
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "RECEIVED_FROM_CSMS UnlockConnectorRequest REQ: {} " + request);
        System.out.println(UnlockStatusEnumType.UnlockFailed.name() + " " + UnlockStatusEnumType.UnlockFailed);
        return new UnlockConnectorResponse(UnlockStatusEnumType.UnlockFailed);


    }
}
