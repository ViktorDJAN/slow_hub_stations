package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;



import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.ocpp_charge_point.TransactionManager;
import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;


import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class Authorize extends OcppOperation {

//    private final ChargePointOcpp chargePointOcpp;
//    private final MeasurandEnumType idTag;
//    private final int connectorId;
    private  boolean isStart; // custom
    private final AuthorizeRequest request;

    public Authorize(AuthorizeRequest request) {
        this.request = request;
    }



    public void sendRequest( )  {
//        AuthorizeRequest request = coreProfile.createAuthorizeRequest(idTag);

        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleAuthorizeResponse);
        } catch (OccurenceConstraintException | UnsupportedFeatureException e) {
            throw new RuntimeException(e);
        }
        LogHandler.loggerOcpp.info("Authorize REQ: {}", request);
    }

    private void handleAuthorizeResponse(Confirmation confirmation, Throwable throwable) {
        try {
            if (throwable != null) {
                LogHandler.logOcpp(throwable);
            } else {
                AuthorizeConfirmation mes = (AuthorizeConfirmation) confirmation;
                switch (mes.getIdTagInfo().getStatus()) {
                    case Accepted:
                        // TODO Начать зарядку
                        if (isStart) {
//                            chargePointOcpp.sendStartTransaction(connectorId, idTag, 0,0);
                        } else {
                            File directory = new File(OcppConfigs.TRANSACTIONS_DIRECTORY);
                            File[] transactionFiles = directory.listFiles();
                            if (transactionFiles != null) {
                                for (File c : transactionFiles) {
                                    int transactionId = Integer.parseInt(c.getName());
                                    Properties tProperties;
                                    tProperties = TransactionManager.getTransactionProperties(transactionId);
                                    int connectorId = Integer.parseInt(tProperties.getProperty("connectorId", "-1"));
//                                    if ((connectorId == this.connectorId)) {          // custom
//                                        chargePointOcpp.sendStopTransaction(transactionId, this.idTag);
//                                    }
                                }
                            }
                        }
                        break;
                    case Blocked:
                    case Expired:
                    case Invalid:
                    case ConcurrentTx:
                        // TODO Зарядка запрещена
                        break;
                }
                LogHandler.loggerOcpp.info("Authorize RESP: {}", mes);
            }
        } catch (IOException e) {
            LogHandler.logOcpp(e);
        }
    }
}
