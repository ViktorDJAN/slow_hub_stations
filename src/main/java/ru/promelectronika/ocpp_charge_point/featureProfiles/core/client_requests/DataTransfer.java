package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;


public class DataTransfer extends OcppOperation {
    private final DataTransferRequest request;

    public DataTransfer(DataTransferRequest request) {
        this.request = request;
    }

    public void sendRequest( ) {
        String messageId = request.getMessageId();
        String data = request.getData();
        if ( messageId != null) {
            request.setMessageId(messageId);
        }
        if (data != null) {
            request.setData(data);
        }
        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleDataTransferResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
            throw new RuntimeException(e);
        }

        LogHandler.loggerOcpp.info("DataTransfer REQ: {}", request);
    }

    private void handleDataTransferResponse(Confirmation confirmation, Throwable throwable) {
        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            AuthorizeConfirmation mes = (AuthorizeConfirmation) confirmation;
            switch (mes.getIdTagInfo().getStatus()) {
                case Accepted:
                    // Начать зарядку
                    break;
                case Blocked:
                case Expired:
                case Invalid:
                case ConcurrentTx:
                    // Зарядка запрещена
                    break;
            }
            LogHandler.loggerOcpp.info("DataTransfer RESP: {}", mes);
        }
    }
}
