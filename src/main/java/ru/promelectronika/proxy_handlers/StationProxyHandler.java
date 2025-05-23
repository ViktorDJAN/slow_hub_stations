package ru.promelectronika.proxy_handlers;


import ru.promelectronika.logHandler.LogHandler;
import ru.promelectronika.ocpp_charge_point.configuration.TransactionInfo;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.model.SlowChargingStation;
import ru.promelectronika.dataBases.ControllersParamsDataBase;
import ru.promelectronika.dataBases.InnerEnMeterDtoDataBase;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.queues.ProxyQueue;
import ru.promelectronika.dto.TransferredCurrent;
import ru.promelectronika.dto.records.ProxyCommandDto;
import ru.promelectronika.dto.ControllerParamsDto;
import ru.promelectronika.dto.records.EvseDto;
import ru.promelectronika.dto.records.MeterValuesDto;
import ru.promelectronika.enums.HandlerEnumType;
import ru.promelectronika.enums.ProxyCommandsEnumType;
import ru.promelectronika.queues.TransactionsQueue;

import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;


public class StationProxyHandler extends AbstractProxyHandler implements Runnable {
    private final long terminationInterval = 30_000;
    private final SlowChargingStation station;
    private int connectorPreviousState;
    private int connectorCurrentState;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService scheduledExecutorService2 = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService scheduledExecutorService3 = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService scheduledExecutorService4 = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> sendMetersValueFuture = null;//
    private ScheduledFuture<?> sendRpcSetLimitFuture = null;//

    private final BooleanSupplier isMeterValueFutureAlive = () -> (sendMetersValueFuture != null && !sendMetersValueFuture.isDone());
    private final BooleanSupplier isSendRpcSetLimitFutureAlive = () -> (sendRpcSetLimitFuture != null && !sendRpcSetLimitFuture.isDone());
    private final BooleanSupplier isTransactionInQueue = () -> (TransactionsQueue.queue.peekFirst() != null);
    private final static Integer INTERVAL = 5; // frequency of sending MeterValues messages
    private final Integer stationHandlerID;


    public StationProxyHandler(SlowChargingStation station) {
        this.station = station;
        stationHandlerID = Integer.parseInt(station.getMode3ClientAddress().substring(10));
    }


    @Override
    public void run() {
        initializePowerOnControllerState();
        scheduledExecutorService3.scheduleAtFixedRate(()->{
            processReceivedCommand(receiveCommand());

        },0, 400, TimeUnit.MILLISECONDS);
        scheduledExecutorService2.scheduleAtFixedRate(()->{
            sendCommand(checkControllerStateChanges());
        },0, 50, TimeUnit.MILLISECONDS);


    }

    /**
     * PROCESSING COMMAND GOTTEN FORM OTHER HANDLERS
     */
    public void processReceivedCommand(ProxyCommandDto receivedCommand) {
        try {
            if (receivedCommand != null) {
                if (receivedCommand.data() instanceof EvseDto dto) {
                    if (dto.connectorState() != null) connectorCurrentState = dto.connectorState();

                    if (receivedCommand.command() == (ProxyCommandsEnumType.START_POWER_SUPPLY.getValue())
                            && dto.evseId().equals(station.getEvseId())) {

                        startCharging(dto.evseId());
                        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.MODE3_LOGGER,
                                stationHandlerID + ":  STATION_PROXY_HANDLER: START_POWER_SUPPLY! : GOT DTO" + dto + " TRANSACTION_QUEUE: "+ TransactionsQueue.queue );
                    }


                    if (receivedCommand.command() == (ProxyCommandsEnumType.STOP_POWER_SUPPLY.getValue())
                            && dto.evseId().equals(station.getEvseId())) {

                        terminateSendingMetricsFutures();
                        if(dto.connectorState()!=null)connectorCurrentState = dto.connectorState();
                        LoggerPrinter.logAndPrint(ColorKind.RED_TEXT, LoggerType.MODE3_LOGGER,
                                stationHandlerID + ":  STATION_PROXY_HANDLER: STOP_POWER_SUPPLY! : GOT DTO" + dto + " TRANSACTION_QUEUE: "+ TransactionsQueue.queue);
                    }
                }
                if (receivedCommand.command() == (ProxyCommandsEnumType.NETWORK_ERROR.getValue())) {
                    terminateSendingMetricsFutures();
                    TransactionsQueue.queue.clear();
                    LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER,
                            stationHandlerID + ":  STATION_PROXY_HANDLER: NETWORK_ERROR! : TRANSACTION_QUEUE: "+ TransactionsQueue.queue );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * CHECKS ANY CHANGES RELATED TO THE CHARGE CONTROLLER, SUCH AS CABLE IN/OUT, and so on
     */
    public ProxyCommandDto checkControllerStateChanges() {
        BooleanSupplier isEmergencyPressed = () -> connectorCurrentState == ProxyCommandsEnumType.FAULT.getValue();
        BooleanSupplier isStopCommand = () -> connectorCurrentState == ProxyCommandsEnumType.STOP.getValue();
        BooleanSupplier isTransactionNotFinished = () -> !TransactionsQueue.queue.isEmpty();

        Integer controllerId = Integer.parseInt(station.getMode3ClientAddress().substring(10));
        if (ControllersParamsDataBase.map.containsKey(controllerId)) {
            ControllerParamsDto paramsDto = ControllersParamsDataBase.map.get(controllerId);
            Integer connectorState = paramsDto.getChargeState();
            connectorPreviousState = connectorCurrentState;
            connectorCurrentState = connectorState;
            if (connectorCurrentState != (connectorPreviousState)) {
                LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_RED_TEXT, LoggerType.MODE3_LOGGER, "checkControllerState() ======= TRANSACTION_QUEUE = "+ TransactionsQueue.queue);

                // ** EMERGENCY BUTTON PRESSED
                if (isEmergencyPressed.getAsBoolean() && isTransactionNotFinished.getAsBoolean()) {
                    terminateSendingMetricsFutures();
                    LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  EMERGENCY_BUTTON_PRESSED!");
                    return new ProxyCommandDto(HandlerEnumType.OCPP, ProxyCommandsEnumType.EMERGENCY_BUTTON_PRESSED.getValue(), null);
                }

                // ** THE CHARGING TRANSACTION IS NORMALLY COMPLETED
                if (isStopCommand.getAsBoolean() && isTransactionNotFinished.getAsBoolean()) {
                    terminateSendingMetricsFutures();
                    LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  THE CHARGING TRANSACTION IS NORMALLY COMPLETED! : " + TransactionsQueue.queue);
                    return new ProxyCommandDto(HandlerEnumType.OCPP, ProxyCommandsEnumType.STOP.getValue(), null);
                }
                // ** ANY_STATE_CHANGES
                var dto = new EvseDto(station.getEvseId(), station.getMode3Client().getConnectorId(), connectorState, ZonedDateTime.now().toString());
                return new ProxyCommandDto(HandlerEnumType.OCPP, dto.connectorState(), dto);
            }
            // IF_CONNECTION OF CONTROLLER IS LOST
            scheduledExecutorService4.schedule(this::terminateTransactionInInterval,30_000,TimeUnit.MILLISECONDS);
//            terminateTransactionInInterval();// In case connection lose , it will be fulfilled in 30 seconds
        }
        return null;
    }


    /**
     * The method is designed for terminating transaction in case connection with Mode3_Ctrl is lost
     */
    public void terminateTransactionInInterval() {
//        if (!station.getMode3Client().isConnected() && !TransactionsQueue.queue.isEmpty()) {
//            timeout((int) terminationInterval);
//        }
        if (!station.getMode3Client().isConnected() && !TransactionsQueue.queue.isEmpty()) {
            LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, "MODE3 hash= " + station.getMode3Client().hashCode());

            LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER: TERMINATED WITH INTERVAL: " + terminationInterval + " seconds ");
            var dto = new EvseDto(station.getEvseId(), station.getMode3Client().getConnectorId(), ProxyCommandsEnumType.STOP.getValue(), ZonedDateTime.now().toString());
            sendCommand(new ProxyCommandDto(HandlerEnumType.OCPP, dto.connectorState(), dto));
            terminateSendingMetricsFutures();
        }
    }


    public void startCharging(Integer evseId) throws InterruptedException {
        if (!TransactionsQueue.queue.isEmpty()) {
            LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": ___________________________STATION_PROXY_HANDLER: startCharging(): TRANSACTION_QUEUE IS NOT EMPTY" + TransactionsQueue.queue);

            for (TransactionInfo info : TransactionsQueue.queue) {
                if (info.getRequest().getEvse().getId().equals(evseId) && info.isStarted()) {
                    station.getMode3Client().rpcSetCurrentLimits();
                    station.getMode3Client().rpcAuthorize();

                    LogHandler.loggerMode3Sending.info(stationHandlerID + ": STATION_PROXY_HANDLER: AUTHORIZE");
                    ColorTuner.printBlackText(stationHandlerID + ": STATION_PROXY_HANDLER: AUTHORIZE");

                    if (sendMetersValueFuture == null) {
                        sendMetersValueFuture = sendMeterValuesToOcppHandler(INTERVAL);
                    } else {
                        sendMetersValueFuture.cancel(true);
                        sendMetersValueFuture = sendMeterValuesToOcppHandler(INTERVAL);
                    }
                    // send RpcSetLimits
                    if (sendRpcSetLimitFuture == null) {
                        sendRpcSetLimitFuture = sendRpcSetCurrentLimits();
                    } else {
                        sendRpcSetLimitFuture.cancel(true);
                        sendRpcSetLimitFuture = sendRpcSetCurrentLimits();
                    }
                    LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_RED_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  SEND_METER_VALUE_FUTURE   STARTED: IS_DONE: " + sendMetersValueFuture.isDone() + " " + sendMetersValueFuture.hashCode());
                    LoggerPrinter.logAndPrint(ColorKind.WHITE_BG_RED_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  SET_CURRENT_LIMITS   STARTED: IS_DONE: " + sendRpcSetLimitFuture.isDone() + " " + sendRpcSetLimitFuture.hashCode());


                }
            }
        }else{
            LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER: startCharging(): TRANSACTION_QUEUE IS  EMPTY" + TransactionsQueue.queue);



        }
    }


    public void terminateSendingMetricsFutures() {
        LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ":!!! STATION_PROXY_HANDLER: Terminate Sending Metrics Futures method()");

        try {
            if (isMeterValueFutureAlive.getAsBoolean() && isSendRpcSetLimitFutureAlive.getAsBoolean()) {
                sendMetersValueFuture.cancel(true);
                sendRpcSetLimitFuture.cancel(true);
                station.getMode3Client().rpcUserStop();

                LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  USER_STOP_SENT________________________" + station.getMode3Client().getSocket().getRemoteSocketAddress());
                LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  SEND_METER_VALUE_FUTURE IS CANCELLED: " + sendMetersValueFuture.isCancelled() + sendMetersValueFuture.hashCode());
                LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  SET_CURRENT_LIMITS IS CANCELLED: " + sendRpcSetLimitFuture.isCancelled() + sendRpcSetLimitFuture.hashCode());

                LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  SEND_METER_VALUE_FUTURE IS DONE: " + sendMetersValueFuture.isDone() + sendMetersValueFuture.hashCode());
                LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, stationHandlerID + ": STATION_PROXY_HANDLER:  SET_CURRENT_LIMITS IS DONE: " + sendRpcSetLimitFuture.isDone() + sendRpcSetLimitFuture.hashCode());


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ScheduledFuture<?> sendMeterValuesToOcppHandler(int interval) {
        return scheduledExecutorService.scheduleAtFixedRate((new Runnable() {
            double deliveredPower = 0.0f;

            @Override
            public void run() {
                try {
                    if (TransactionsQueue.queue.peekFirst() != null) {
                        int innerControllerId = Integer.parseInt(station.getMode3ClientAddress().substring(10));
                        double currentConsumedPower = Math.round((InnerEnMeterDtoDataBase.map.get(innerControllerId).getConsumedPower()));
                        deliveredPower += currentConsumedPower / 3600;
                        double scale = Math.pow(10, 2);
                        double scaledDeliveryPower = Math.ceil(deliveredPower * scale) / scale;
                        double consumedPower_kW = currentConsumedPower / 1000;
                        double scaledConsumedPower_kW = Math.ceil(consumedPower_kW * scale) / scale;
                        MeterValuesDto dto = new MeterValuesDto(station.getEvseId(), scaledDeliveryPower * interval, scaledConsumedPower_kW);
                        sendCommand(new ProxyCommandDto(HandlerEnumType.OCPP, ProxyCommandsEnumType.START_POWER_SUPPLY.getValue(), dto));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }), 0, interval, TimeUnit.SECONDS);

    }


    public ScheduledFuture<?> sendRpcSetCurrentLimits() {
        return scheduledExecutorService.scheduleAtFixedRate((new Runnable() {
            private volatile double previousCurrent = 0;
            private volatile double actualCurrent = 0;

            @Override
            public void run() {
                previousCurrent = actualCurrent;
                actualCurrent = TransferredCurrent.maximumCurrentLimitA;
                if (TransferredCurrent.maximumCurrentLimitA > 0.0 && actualCurrent != previousCurrent) {
                    station.getMode3Client().rpcSetCurrentLimits();

                    LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER,
                            stationHandlerID + ": STATION_PROXY_HANDLER: CURRENT CHANGED! Current Limits SENT to the CHARGE_CONTROLLER:" + actualCurrent);
                }
            }
        }), 0, 1, TimeUnit.SECONDS);

    }


    @Override
    public ProxyCommandDto receiveCommand() {
        for (ProxyCommandDto dto : ProxyQueue.queue) {
            if (dto.enumType().equals(HandlerEnumType.STATION)) {
                LoggerPrinter.logAndPrint(ColorKind.BLACK_BG_CYAN_TEXT, LoggerType.MODE3_LOGGER,
                        stationHandlerID + ": STATION_PROXY_HANDLER: RECEIVED  ProxyCommandDto: -- " + dto + " MAP_SIZE: " + ProxyQueue.queue.size());
                ProxyQueue.queue.removeFirstOccurrence(dto);
                return dto;
            }
        }
        return null;
    }


    @Override
    public void sendCommand(ProxyCommandDto dto) {
        BooleanSupplier isDtoNotNull = () -> dto != null;
        if (isDtoNotNull.getAsBoolean()) {
            ProxyQueue.queue.addLast(dto);
            LoggerPrinter.logAndPrint(ColorKind.BLACK_BG_CYAN_TEXT, LoggerType.MODE3_LOGGER,
                    stationHandlerID + ": STATION_PROXY_HANDLER: SENT ProxyCommand: " + dto + " ProxyQueue.queue.size_SIZE: " + ProxyQueue.queue.size());
        }
    }


    public void initializePowerOnControllerState() {
        try {
            LoggerPrinter.logAndPrint(ColorKind.BLACK_BG_CYAN_TEXT, LoggerType.MODE3_LOGGER,
                    stationHandlerID + ": STATION_PROXY_HANDLER: CHARGING CONTROLLER INITIALIZED");
            station.getMode3Client().rpcPing();
            connectorCurrentState = 100;
        } catch (Exception e) {
            connectorCurrentState = 7;
        }
    }


    public void timeout(int value_ms) {
        try {
            Thread.sleep(value_ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long getTerminationInterval() {
        return terminationInterval;
    }

    public SlowChargingStation getStation() {
        return station;
    }

    public int getConnectorPreviousState() {
        return connectorPreviousState;
    }

    public void setConnectorPreviousState(int connectorPreviousState) {
        this.connectorPreviousState = connectorPreviousState;
    }

    public int getConnectorCurrentState() {
        return connectorCurrentState;
    }

    public void setConnectorCurrentState(int connectorCurrentState) {
        this.connectorCurrentState = connectorCurrentState;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public ScheduledExecutorService getScheduledExecutorService2() {
        return scheduledExecutorService2;
    }

    public ScheduledExecutorService getScheduledExecutorService3() {
        return scheduledExecutorService3;
    }

    public ScheduledExecutorService getScheduledExecutorService4() {
        return scheduledExecutorService4;
    }

    public ScheduledFuture<?> getSendMetersValueFuture() {
        return sendMetersValueFuture;
    }

    public void setSendMetersValueFuture(ScheduledFuture<?> sendMetersValueFuture) {
        this.sendMetersValueFuture = sendMetersValueFuture;
    }

    public ScheduledFuture<?> getSendRpcSetLimitFuture() {
        return sendRpcSetLimitFuture;
    }

    public void setSendRpcSetLimitFuture(ScheduledFuture<?> sendRpcSetLimitFuture) {
        this.sendRpcSetLimitFuture = sendRpcSetLimitFuture;
    }

    public BooleanSupplier getIsMeterValueFutureAlive() {
        return isMeterValueFutureAlive;
    }

    public BooleanSupplier getIsSendRpcSetLimitFutureAlive() {
        return isSendRpcSetLimitFutureAlive;
    }

    public BooleanSupplier getIsTransactionInQueue() {
        return isTransactionInQueue;
    }

    public Integer getStationHandlerID() {
        return stationHandlerID;
    }
}
