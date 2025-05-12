package ru.promelectronika.ocpp_charge_point.featureProfiles.core;

import eu.chargetime.ocpp.feature.profile.ClientCoreEventHandler;
import eu.chargetime.ocpp.model.core.*;
import eu.chargetime.ocpp.model.core_2_0_1.messages.*;
import eu.chargetime.ocpp.model.core_2_0_1.messages.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.UnlockConnectorRequest;
import lombok.Setter;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;

import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.featureProfiles.core.csms_requests_handlers.*;


/**
 * KIND OF "SERVER"
 */
// CONFIRMATION is an OUTGOING RESPONSE
// RESPONSES are MOSTLY STATUSES excluding several methods here
// Implements all possible events for each of them we should compound requests !!!
// That class process requests gotten from central system and sends it back
@SuppressWarnings("NonAsciiCharacters")
@Setter
public class СsmsRequestsHandler implements ClientCoreEventHandler {

    private ChargePointOcpp chargePointOcpp;

    public СsmsRequestsHandler(ChargePointOcpp chargePointOcpp) {
        this.chargePointOcpp = chargePointOcpp;
    }


    @Override
    public ChangeAvailabilityResponse handleChangeAvailabilityRequest(ChangeAvailabilityRequest request) {
        LogHandler.loggerOcpp.info("ChangeAvailabilityRequestHandler REQ: {}", request);
        ChangeAvailabilityResponse response = new ChangeAvailabilityRequestHandler(request).getResponse();
        LogHandler.loggerOcpp.info("ChangeAvailabilityRequestHandler SEND: {}", response);
        return response;
    }

    @Override
    public RequestStartTransactionResponse handleRemoteStartTransactionRequest(RequestStartTransactionRequest request) {
        LogHandler.loggerOcpp.info("RemoteStartTransactionRequestHandler REQ: {}", request);
        RequestStartTransactionResponse response = new RemoteStartTransactionRequestHandler(request, chargePointOcpp).getResponse();
        LogHandler.loggerOcpp.info("RemoteStartTransactionRequestHandler SEND: {}", response);
        return response;
    }

    @Override
    public RequestStopTransactionResponse handleRemoteStopTransactionRequest(RequestStopTransactionRequest request) {
        LogHandler.loggerOcpp.info("RemoteStopTransactionRequestHandler REQ: {}", request);
        RequestStopTransactionResponse response = new RemoteStopTransactionRequestHandler(request, chargePointOcpp).getResponse();
        LogHandler.loggerOcpp.info("RemoteStopTransactionRequestHandler SEND: {}", response);
        return response;
    }

    @Override
    public ResetConfirmation handleResetRequest(ResetRequest request) {
        LogHandler.loggerOcpp.info("ResetRequestHandler REQ: {}", request);
        ResetConfirmation response = new ResetRequestHandler(request, chargePointOcpp).getResponse();
        LogHandler.loggerOcpp.info("ResetRequestHandler SEND: {}", response);
        return response;
    }


    @Override
    public UnlockConnectorResponse handleUnlockConnectorRequest(UnlockConnectorRequest request) {
        LogHandler.loggerOcpp.info("UnlockConnectorRequestHandler REQ: {}", request);
        UnlockConnectorResponse response = new UnlockConnectorRequestHandler(request, chargePointOcpp).getResponse();
        LogHandler.loggerOcpp.info("UnlockConnectorRequestHandler SEND: {}", response);
        return response;
    }

    @Override
    public GetBaseReportResponse handleGetBaseReportRequest(GetBaseReportRequest request) {
        LogHandler.loggerOcpp.info("GetBaseReportRequestHandler REQ: {}", request);
        GetBaseReportResponse response = new GetBaseReportRequestHandler(request, chargePointOcpp).formResponse();
        LogHandler.loggerOcpp.info("GetBaseReportRequestHandler SEND: {}", response);
        return null;
    }



// TODO !!!      NOT IMPLEMENTED YET_____________________________________________________________________________________
    //todo >> It is needed to change a concrete configuration
    @Override
    public ChangeConfigurationConfirmation handleChangeConfigurationRequest(ChangeConfigurationRequest request) {
        LogHandler.loggerOcpp.info("ChangeConfigurationREQ: {}", request);
        ChangeConfigurationConfirmation response = new ChangeConfigurationRequestHandler(request).getResponse();
        LogHandler.loggerOcpp.info("ChangeConfigurationRequestHandler SEND: {}", response);
        return response;
    }

    // todo >> It is need for getting all list of configurations
    @Override
    public GetConfigurationConfirmation handleGetConfigurationRequest(GetConfigurationRequest request) {
        LogHandler.loggerOcpp.info("GetConfigurationRequestHandler REQ: {}", request);
        GetConfigurationConfirmation response = new GetConfigurationRequestHandler(request).getResponse();
        LogHandler.loggerOcpp.info("GetConfigurationRequestHandler SEND: {}", response);
        return response;
    }


    //TODO сделать
    @Override
    public ClearCacheConfirmation handleClearCacheRequest(ClearCacheRequest request) {
        LogHandler.loggerOcpp.info("ClearCacheRequest REQ: {}", request);
        ClearCacheConfirmation response = new ClearCacheRequestHandler(request).getResponse();
        LogHandler.loggerOcpp.info("ClearCacheRequest SEND: {}", response);
        return response;
    }


    //TODO сделать
    @Override
    public DataTransferConfirmation handleDataTransferRequest(DataTransferRequest request) {
        LogHandler.loggerOcpp.info("DataTransfer REQ: {}", request);
        DataTransferConfirmation response = new DataTransferConfRequestHandler(request).getResponse();
        LogHandler.loggerOcpp.info("DataTransfer SEND: {}", response);
        return response;
    }


}
