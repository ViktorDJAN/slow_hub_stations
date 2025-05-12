package ru.promelectronika.ocpp_charge_point.featureProfiles.core.client_requests;

import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.core_2_0_1.messages.BootNotificationRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.BootNotificationResponse;
import eu.chargetime.ocpp.model.core_2_0_1.messages.HeartbeatRequest;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.ocpp_charge_point.OcppOperation;
import ru.promelectronika.logHandler.LogHandler;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// OCPP 2.0.1
// From Charge point sends to the Central System
public class BootNotification extends OcppOperation {

    private final BootNotificationRequest request;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> heartbeatSendingFuture;


    public BootNotification(BootNotificationRequest request) {
        this.request = request;
    }

    public void sendRequest() {
        try {
            super.getChargePointOcpp().getClient().send(request).whenComplete(this::handleBootNotificationResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "BootNotification REQ: {} " + request);

    }

    private void handleBootNotificationResponse(Confirmation confirmation, Throwable throwable) {
        if (throwable != null) {
            LogHandler.logOcpp(throwable);
        } else {
            BootNotificationResponse response = (BootNotificationResponse) confirmation;
            Integer interval = response.getInterval();
            if (interval == 0) {
                interval = 30; // arbitrary chosen interval, cause of CSMS server specifies needed for it interval
            }
            try {
                switch (response.getStatus()) {
                    case Rejected:
                        super.getChargePointOcpp().getClient().disconnect();
                    case Pending:
                        new Thread(getReconnect(interval)).start();
                        break;
                    case Accepted:
                        startHeartbeatSending(interval);
                }
            } catch (Throwable e) {
                LogHandler.logOcpp(e);
            }
            LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_BLUE_TEXT, LoggerType.OCPP_LOGGER, "BootNotification RESP: {} "+ response);

        }
    }

    private void startHeartbeatSending(Integer interval) {
        heartbeatSendingFuture = service.scheduleAtFixedRate(() -> {
            new Heartbeat(new HeartbeatRequest()).sendRequest();
            if (!getChargePointOcpp().isConnected() && heartbeatSendingFuture != null) {
                LoggerPrinter.logAndPrint(ColorKind.RED_BG_BLACK_TEXT, LoggerType.OCPP_LOGGER, "HEARTBEAT_SENDING_FUTURE IS DONE: " + heartbeatSendingFuture.isDone());
                heartbeatSendingFuture.cancel(true);
            }
        }, interval, interval, TimeUnit.SECONDS);


    }


    private Runnable getReconnect(int interval) {
        return () -> {
            try {
                Thread.sleep(interval * 1000L);
                super.getChargePointOcpp().connect();
            } catch (InterruptedException e) {
                LogHandler.logOcpp(e);
            }
        };
    }
}
