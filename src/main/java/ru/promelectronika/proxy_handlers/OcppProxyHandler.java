package ru.promelectronika.proxy_handlers;


import eu.chargetime.ocpp.model.core_2_0_1.enumerations.ChargingStateEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.ConnectorStatusEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.TransactionEventEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.enumerations.TriggerReasonEnumType;
import eu.chargetime.ocpp.model.core_2_0_1.messages.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core_2_0_1.messages.TransactionEventRequest;
import lombok.Getter;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.queues.ProxyQueue;
import ru.promelectronika.dto.records.ProxyCommandDto;
import ru.promelectronika.dto.records.EvseDto;
import ru.promelectronika.dto.records.MeterValuesDto;
import ru.promelectronika.enums.HandlerEnumType;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.enums.ProxyCommandsEnumType;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.ocpp_charge_point.RequestBuilder;
import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;
import ru.promelectronika.queues.TransactionsQueue;

import java.util.function.BooleanSupplier;

@Getter
public class OcppProxyHandler extends AbstractProxyHandler implements Runnable {
    private final ChargePointOcpp chargePointOcpp;
    private int transactionIDsCounter = 0;
    int messageCounter = 0;


    public OcppProxyHandler(ChargePointOcpp chargePointOcpp) {
        this.chargePointOcpp = chargePointOcpp;
    }

    @Override
    public void run() {


        while (true) {
            try {
                checkConnectionLose();
                ProxyCommandDto dto = receiveCommand();
                processCommand(dto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void checkConnectionLose() {
        try {
            if (!chargePointOcpp.isConnected() && messageCounter < 1) {
                LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.OCPP_LOGGER, "OCPP_PROXY_HANDLER: CONNECTION_LOST");
                ColorTuner.purpleBackgroundBlackText("Connection lost");
                sendCommand(new ProxyCommandDto(HandlerEnumType.STATION, ProxyCommandsEnumType.NETWORK_ERROR.getValue(), null));
                messageCounter++;
            }
            if (chargePointOcpp.isConnected()) {
                messageCounter = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void sendCommand(ProxyCommandDto dto) {
        BooleanSupplier isDtoNotNull = () -> dto != null;
        if (isDtoNotNull.getAsBoolean()) {
            ProxyQueue.queue.addLast(dto);
            LoggerPrinter.logAndPrint(ColorKind.BLACK_BG_YELLOW_TEXT, LoggerType.OCPP_LOGGER, "OCPP_HANDLER SEND: " + dto + "MAP_SIZE: " + ProxyQueue.queue.size());

        }
    }

    @Override
    public ProxyCommandDto receiveCommand() {
        for (ProxyCommandDto commandDto : ProxyQueue.queue) {
            if (commandDto.enumType().equals(HandlerEnumType.OCPP)) {
                LoggerPrinter.logAndPrint(ColorKind.BLACK_BG_YELLOW_TEXT, LoggerType.OCPP_LOGGER, "OCPP_HANDLER RECEIVED: " + commandDto + " MAP_SIZE: " + ProxyQueue.queue.size());
                ProxyQueue.queue.removeFirstOccurrence(commandDto);
                return commandDto;
            }
        }
        return null;
    }

    private void finishCertainTransaction(EvseDto dto) {
        System.out.println("CAME TO CERTAIN TRANSACTION FINISH METHOD....");
        try {
            if (TransactionsQueue.queue != null && !TransactionsQueue.queue.isEmpty()) {
                ColorTuner.purpleBackgroundBlackText("finishCertainTransaction OCppProxyHandler________________________________");
                TransactionsQueue.queue.removeIf(transactionInfo ->
                        transactionInfo.getRequest().getEvse().getId() == dto.evseId() &&
                                transactionInfo.getRequest().getEvse().getConnectorId() == dto.connectorId()
                );
            }else {
                ColorTuner.purpleBackgroundBlackText("CAN NOT!!!!   finishCertainTransaction OCppProxyHandler_" + TransactionsQueue.queue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processCommand(ProxyCommandDto commandDto) {

        if (commandDto != null) {

            if (commandDto.data() instanceof EvseDto dto) {
                switch (dto.connectorState()) {

                    case 0 -> { // DISCONNECTED
                        try {
                            StatusNotificationRequest request = RequestBuilder.buildStatusNotificationRequest(ConnectorStatusEnumType.Available, dto.evseId(), dto.connectorId());
                            chargePointOcpp.sendStatusNotificationRequest(request);
                            finishCertainTransaction(dto);
                            ColorTuner.blackBackgroundRedText(dto);
                            ColorTuner.printGreenText("TRANSACTIONS   : " + TransactionsQueue.queue);
                        } catch (Exception e) {
                            System.out.println("EXCEPTION: " + e);
                        }
                    }
                    case 1 -> { // CONNECTED
                        // OCPP CONNECTORS STATE NOTIFYING
                        StatusNotificationRequest request = RequestBuilder.buildStatusNotificationRequest(ConnectorStatusEnumType.Occupied, dto.evseId(), dto.connectorId());
                        chargePointOcpp.sendStatusNotificationRequest(request);
                        // OCPP TRANSACTION BEGINNING
                        String transactionId = "" + ++transactionIDsCounter;
                        TransactionEventRequest transactionRequest = RequestBuilder.buildTransactionEventRequest(TransactionEventEnumType.Started, TriggerReasonEnumType.CablePluggedIn,
                                ChargingStateEnumType.EVConnected, dto.evseId(), dto.connectorId(), transactionId);

                        TransactionInfo transactionInfo = new TransactionInfo(transactionId, transactionRequest);
                        TransactionsQueue.queue.addLast(transactionInfo);
                        chargePointOcpp.sendTransactionEventRequest(transactionRequest);
                        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.OCPP_LOGGER, "OCPP_PROXY_HANDLER: TRANSACTION STARTED: " + TransactionsQueue.queue);
                        ColorTuner.printGreenText("TRANSACTIONS   : " + TransactionsQueue.queue);
                    }
                    case 5 -> {
                        ColorTuner.printGreenText("TRANSACTIONS   : " + TransactionsQueue.queue);
                        TransactionEventRequest request = RequestBuilder.buildLocalStopTransactionEventRequest();
                        ColorTuner.redBackgroundBlackText(request);
                        chargePointOcpp.sendStopTransactionRequest(request);
                        finishCertainTransaction(dto);
//                        finishTransaction(request);
                        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.OCPP_LOGGER, "OCPP_PROXY_HANDLER: EV CHARGING IS COMPLETE!");

                    }
                    case 6, 7 -> {
                        StatusNotificationRequest request = RequestBuilder.buildStatusNotificationRequest(ConnectorStatusEnumType.Faulted, dto.evseId(), dto.connectorId());
                        chargePointOcpp.sendStatusNotificationRequest(request);
                        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.OCPP_LOGGER, "OCPP_PROXY_HANDLER: EV CHARGING IS FAILED!");
                    }
                    default -> {
                        LoggerPrinter.logAndPrint(ColorKind.BLACK_BG_YELLOW_TEXT, LoggerType.OCPP_LOGGER, "OCPP_HANDLER UNPROCESSED_COMMAND!: " + commandDto + " MAP_SIZE: " + ProxyQueue.queue.size());
                    }
                }
            }
            if (commandDto.data() instanceof MeterValuesDto dto) {
                TransactionEventRequest request = RequestBuilder.buildTransactionEventRequestForMeterValueSending(dto);
                chargePointOcpp.sendTransactionEventRequest(request);
            }

            // commands emergency stop
            if (commandDto.command() == ProxyCommandsEnumType.EMERGENCY_BUTTON_PRESSED.getValue()) {
                LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.OCPP_LOGGER, "OCPP_PROXY_HANDLER: EMERGENCY_BUTTON_PRESSED");
                TransactionEventRequest request = RequestBuilder.buildLocalStopTransactionEventRequest();
                finishTransaction(request);
            }

        }
    }

    private void finishTransaction(TransactionEventRequest request) {
        if (!TransactionsQueue.queue.isEmpty()) {
            TransactionsQueue.queue.removeFirst();
            System.out.println("Size: " + TransactionsQueue.queue.size());
            chargePointOcpp.sendTransactionEventRequest(request);
        }
    }

    public void timeout(int value_ms) {
        try {
            Thread.sleep(value_ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


