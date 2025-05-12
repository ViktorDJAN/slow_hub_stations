package ru.promelectronika.ocpp_charge_point.featureProfiles.remoteTrigger.confirmation;

import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core_2_0_1.messages.HeartbeatRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageStatus;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.ocpp_charge_point.ConnectorManager;
import ru.promelectronika.ocpp_charge_point.TransactionManager;
import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.OcppConfigs;


import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TriggerMessage {
    private final TriggerMessageRequest request;
    private final ChargePointOcpp chargePoint;
    private final ScheduledExecutorService executor;

    public TriggerMessage(TriggerMessageRequest request, ChargePointOcpp chargePoint) {
        this.request = request;
        this.chargePoint = chargePoint;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public TriggerMessageConfirmation getResponse() {
        File directory;
        try {
            switch (request.getRequestedMessage()) {
                case BootNotification:
                    sendBootNotification();
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case Heartbeat:
                    sendHeartbeat();
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case MeterValues:
                    directory = new File(OcppConfigs.TRANSACTIONS_DIRECTORY);
                    File[] transactionFiles = directory.listFiles();
                    if (transactionFiles != null) {
                        int i = 0;
                        for (File c : transactionFiles) {
                            int transactionId = Integer.parseInt(c.getName());
                            Properties tProperties = TransactionManager.getTransactionProperties(transactionId);
                            int connectorId = Integer.parseInt(tProperties.getProperty("connectorId", "-1"));
                            if ((connectorId == request.getConnectorId())) {
                                sendMeterValues(connectorId);
                                i = connectorId;
                            }
                        }
                        if (i == 0) {
                            for (File c : transactionFiles) {
                                int transactionId = Integer.parseInt(c.getName());
                                Properties tProperties = TransactionManager.getTransactionProperties(transactionId);
                                int connectorId = Integer.parseInt(tProperties.getProperty("connectorId", "-1"));
                                sendMeterValues(connectorId);
                            }
                        }
                    }
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case StatusNotification:
                    if (request.getConnectorId() != null) {
                        sendStatusNotification(request.getConnectorId());
                    } else {
                        directory = new File(OcppConfigs.CONNECTORS_DIRECTORY);
                        File[] connectorFiles = directory.listFiles();
                        if (connectorFiles != null) {
                            for (File c : connectorFiles) {
                                int connectorId = Integer.parseInt(c.getName());
                                sendStatusNotification(connectorId);
                            }
                        }
                    }
                    return new TriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                case DiagnosticsStatusNotification:
                case FirmwareStatusNotification:
                default:
                    return new TriggerMessageConfirmation(TriggerMessageStatus.NotImplemented);
            }
        } catch (IOException e) {
            LogHandler.logOcpp(e);
            return new TriggerMessageConfirmation(TriggerMessageStatus.Rejected);
        }
    }

    private void sendBootNotification() throws IOException {
//        ChargingStationType chargingStationType = new ChargingStationType(); //todo  to implement it
//        Runnable myTask = () -> chargePoint.sendBootNotificationRequest(new BootNotificationRequest(BootReasonEnumType.PowerUp,c ));
//        executor.schedule(myTask, 50, TimeUnit.MILLISECONDS);
    }

    private void sendDiagnosticsStatusNotification(int connectorId) {

    }

    private void sendFirmwareStatusNotification(int connectorId) {

    }

    private void sendHeartbeat() throws IOException {
        Runnable myTask = () -> chargePoint.sendHeartbeatRequest(new HeartbeatRequest());
        executor.schedule(myTask, 50, TimeUnit.MILLISECONDS);
    }

    private void sendMeterValues(int transactionId) throws IOException {
        executor.schedule((() -> {
//            new MeterValueSender(transactionId, chargePoint);
        }), 50, TimeUnit.MILLISECONDS);
    }

    private void sendStatusNotification(int connectorId) {
        executor.schedule((() -> {
            try {
                Properties connectorProperties = ConnectorManager.getConnectorProperties(connectorId);
                ChargePointStatus status = ChargePointStatus.valueOf(connectorProperties.getProperty("ChargePointStatus"));
                ChargePointErrorCode errorCode = ChargePointErrorCode.valueOf(connectorProperties.getProperty("ChargePointErrorCode"));
//                chargePoint.sendStatusNotification(connectorId,  null);
            } catch (IOException e) {
                LogHandler.logOcpp(e);
            }
        }), 50, TimeUnit.MILLISECONDS);
    }
}
