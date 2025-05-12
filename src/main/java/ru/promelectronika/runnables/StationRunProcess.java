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
    private Future<?> controllerConnectingFuture = null;
    private Future<?> stationProxyHandler = null;
    private Future<?> ocppProxyFuture = null;

    public StationRunProcess(SlowChargingStation chargingStation, ChargePointOcpp chargePointOcpp) {
        this.chargingStation = chargingStation;
        this.chargePointOcpp = chargePointOcpp;
    }

    @Override
    public void run() {
        try {
            startCharging();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // todo add build supplier or predicate. Split handlers start into separated methods
    public void startCharging() throws InterruptedException {
        while (true) {


                startControllerStarter();
//            startOcppProxyHandler();
                startStationProxyHandler();

        }
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

    private void startStationProxyHandler() {
        if (stationProxyHandler == null || stationProxyHandler.isDone()) {
            stationProxyHandler = executorService.submit(new StationProxyHandler(chargingStation));
        }
    }

    private void startControllerStarter() {
        if (controllerConnectingFuture == null || controllerConnectingFuture.isDone()) {
            controllerConnectingFuture = executorService.submit(new ControllerStarter(chargingStation, true));
        }
    }
}
