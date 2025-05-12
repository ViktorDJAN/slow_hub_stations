package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.AuthorizationStatusEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.messages.TransactionEventRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.TransactionEventResponse;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;


public class TransactionEvent extends OcppOperation {
    private final TransactionEventRequest request;

    public TransactionEvent(TransactionEventRequest request) {
        this.request = request;

    }

    public void sendRequest() {
        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleStartTransactionResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
            throw new RuntimeException(e);
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "TransactionEvent REQ: {} " + request);

    }


    private void handleStartTransactionResponse(Confirmation confirmation, Throwable throwable) {

        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            TransactionEventResponse response = (TransactionEventResponse) confirmation;
            System.out.println("RECEIVE: " + response);

            AuthorizationStatusEnumType status = response.getIdTokenInfo().getStatus();
            switch (status) {
                case Accepted:
                    break;
                case Blocked:
                case Expired:
                case Invalid:
                case ConcurrentTx:
                    // TODO можно вывести на экран ошибку начала транзакции
//                    LogHandler.loggerOcpp.info("Transaction ID: {} not started", response.getTransactionId());
                    break;
            }
            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "TransactionEvent RESP: {} "+ response);
        }
    }
}
