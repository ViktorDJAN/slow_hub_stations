package ru.promelectronika.ocpp_charge_point;

import eu.chargetime.ocpp.IClientAPI;
import eu.chargetime.ocpp.feature.profile.ClientCoreProfile;
import eu.chargetime.ocpp.model.RequestWithId;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.*;
import eu.chargetime.ocpp.model.core_2_0_1.messages.*;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;

import ru.promelectronika.ocpp_charge_point.configuration.JsonClientBuilder;
import ru.promelectronika.ocpp_charge_point.featureProfiles.core.CoreOperationEvents;

import ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests.*;
import ru.promelectronika.ocpp_charge_point.featureProfiles.core.СsmsRequestsHandler;
import ru.promelectronika.ocpp_charge_point.featureProfiles.remoteTrigger.TriggerConfirmationEvents;
import ru.promelectronika.ocpp_charge_point.featureProfiles.remoteTrigger.confirmation.TriggerMessage;
import ru.promelectronika.util_stuff.ConfigsFile;

import java.util.function.BooleanSupplier;


@SuppressWarnings("NonAsciiCharacters")

public class ChargePointOcpp implements CoreOperationEvents, TriggerConfirmationEvents {
    private IClientAPI client;
    private final ClientCoreProfile clientCoreProfile; // is a bunch of features (client requests)
    private boolean isConnected = false;
    private final String serverAddress;


    public ChargePointOcpp(String serverAddress) {
        this.clientCoreProfile = new ClientCoreProfile(new СsmsRequestsHandler(this)); // ClientCoreProfile set up handler for client core feature requests.
        this.serverAddress = serverAddress;
    }

    /**
     * RECONNECTION LOGICS IS MADE
     */
    public void connect() {
        BooleanSupplier isClosedClient = ()-> client != null && client.isClosed() ;
        try {
            if (!isConnected || client == null) {
                client = JsonClientBuilder.build(serverAddress, clientCoreProfile);
                if (isClosedClient.getAsBoolean()) {
                    client.connect(serverAddress, new ClientEventHandler(this));
                    System.out.println("CLIENT_OCPP CONNECTED: " + isConnected );
                    BootNotificationRequest request = RequestBuilder.buildBootNotificationRequest(BootReasonEnumType.PowerUp, ConfigsFile.cs_model, ConfigsFile.vendor_name);
                    sendBootNotificationRequest(request);
                }
            }
            if (isClosedClient.getAsBoolean()) {
                client.connect(serverAddress, new ClientEventHandler(this));
                BootNotificationRequest request = RequestBuilder.buildBootNotificationRequest(BootReasonEnumType.PowerUp, ConfigsFile.cs_model, ConfigsFile.vendor_name);
                sendBootNotificationRequest(request);
            }
        } catch (Exception e) {
          e.printStackTrace();
        }

    }


    //////////////////////////////////////////////////CORE OPERATION EVENTS ////////////////////////////////////////////////////
    // CHARGE_POINT ===sends==> CENTRAL_SYSTEM       COMMANDS
    @Override
    public void sendAuthorizeRequest(RequestWithId request) {
        if (request instanceof AuthorizeRequest authorizeRequest) {
            new Authorize(authorizeRequest).sendRequest();
        }
    }

    @Override
    public void sendBootNotificationRequest(BootNotificationRequest request) {
        new BootNotification(request).sendRequest();
    }

    @Override
    public void sendDataTransferRequest(RequestWithId request) {
        if (request instanceof DataTransferRequest dataTransferRequest) {
            new DataTransfer(dataTransferRequest).sendRequest();
        }
    }

    @Override
    public void sendHeartbeatRequest(HeartbeatRequest request) {
        new Heartbeat(request).sendRequest();

    }

    @Override
    public void sendMeterValueRequest(MeterValuesRequest request) {
        new MeterValues(request).sendRequest();

    }

    @Override
    public void sendStatusNotificationRequest(StatusNotificationRequest request) {
        new StatusNotification(request).sendRequest();

    }

    @Override
    public void sendTransactionEventRequest(TransactionEventRequest request) {
        new TransactionEvent(request).sendRequest();

    }

    @Override
    public void sendStopTransactionRequest(TransactionEventRequest request) {
        new TransactionEvent(request).sendRequest();

    }

    @Override
    public void sendNotifyReportRequest(NotifyReportRequest request) {
        new NotifyReport(request).sendRequest();

    }


    ////////////////////////////////////////////////REMOTE TRIGGER CONF/////////////////////////////////////////////////
    @Override
    public TriggerMessageConfirmation triggerMessage(TriggerMessageRequest request) {
        return new TriggerMessage(request, this).getResponse();
    }

    public IClientAPI getClient() {
        return client;
    }

    public void setClient(IClientAPI client) {
        this.client = client;
    }

    public ClientCoreProfile getClientCoreProfile() {
        return clientCoreProfile;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
