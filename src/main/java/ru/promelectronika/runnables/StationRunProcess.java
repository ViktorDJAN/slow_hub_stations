package ru.promelectronika.runnables;


import ru.promelectronika.model.SlowChargingStation;
import ru.promelectronika.ocpp_charge_point.ChargePointOcpp;
import ru.promelectronika.proxy_handlers.OcppProxyHandler;
import ru.promelectronika.proxy_handlers.StationProxyHandler;

import java.util.concurrent.*;


public class StationRunProcess implements Runnable {
    private final SlowChargingStation chargingStation;
    private final ChargePointOcpp chargePointOcpp;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Future<?> controllerConnectingFuture = null;
    private Future<?> stationProxyHandler = null;
    private Future<?> ocppProxyFuture = null;

    public StationRunProcess(SlowChargingStation chargingStation, ChargePointOcpp chargePointOcpp) {
        this.chargingStation = chargingStation;
        this.chargePointOcpp = chargePointOcpp;
    }

    @Override
    public void run() {
        startCharging();
    }


    public void startCharging() {
        scheduledExecutorService.execute(()->{
            startControllerStarterFuture();
            startStationProxyHandlerFuture();
        });
    }

    private void startOcppProxyHandler() {
        if (ocppProxyFuture == null) {
            ocppProxyFuture = executorService.submit(new OcppProxyHandler(chargePointOcpp));
        }
        if (!chargePointOcpp.isConnected()) {
            ocppProxyFuture.cancel(true);
            ocppProxyFuture = null;
        }
    }

    private void startStationProxyHandlerFuture() {
        if (stationProxyHandler == null || stationProxyHandler.isDone()) {
            stationProxyHandler = executorService.submit(new StationProxyHandler(chargingStation));
        }
    }

    private void startControllerStarterFuture() {
        if (controllerConnectingFuture == null || controllerConnectingFuture.isDone()) {
            controllerConnectingFuture = executorService.submit(new ControllerStarter(chargingStation));
        }
    }
}
