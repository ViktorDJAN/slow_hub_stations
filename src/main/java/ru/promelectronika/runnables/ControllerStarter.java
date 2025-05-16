package ru.promelectronika.runnables;


import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ConfigsFile;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.model.SlowChargingStation;
import ru.promelectronika.dataBases.ControllersParamsDataBase;
import ru.promelectronika.dataBases.PingFutureDataBase;
import ru.promelectronika.dataBases.Mode3ControllersDataBase;
import ru.promelectronika.dto.ControllerParamsDto;
import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.rpcClientServer.Mode3Client;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ControllerStarter implements Runnable {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final SlowChargingStation station;
    private final boolean isLoggerOn;
    private ScheduledFuture<?> pingSendFuture;
    private final ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();

    public ControllerStarter(SlowChargingStation slowChargingStation, boolean isLoggerOn) {
        this.station = slowChargingStation;
        this.isLoggerOn = isLoggerOn;
    }


    @Override
    public void run() {
        executorService2.scheduleAtFixedRate(this::connectToController, 0, 3000, TimeUnit.MILLISECONDS);
//        while (!Thread.interrupted()) {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            connectToController();
//        }
    }

    public void connectToController() {
        // if there is finished futures , cancel them
        Integer controllerId = Integer.parseInt(station.getMode3ClientAddress().substring(10));

        if (PingFutureDataBase.map.get(controllerId) != null && PingFutureDataBase.map.get(controllerId).isDone()) {
            PingFutureDataBase.map.get(controllerId).cancel(true);
        }

        if (!Mode3ControllersDataBase.map.containsKey(controllerId)) {
            try {// CREATING CONTROLLER AND SETTING IT TO STATION
                var mode3Client = new Mode3Client(station.getConnectorId());
                mode3Client.connectToSocket(station.getMode3ClientAddress(), station.getMode3ClientPort());
                Mode3ControllersDataBase.map.put(controllerId, mode3Client);
                station.setMode3Client(mode3Client);

                mode3Client.rpcLog(true, ConfigsFile.logger_remote_address,ConfigsFile.logger_remote_port);

                // CREATING AND ADDING CONTROLLER PARAMS DTO TO MAP
                if (mode3Client.getSocket().isConnected()) {
                    mode3Client.  rpcConnectRequest(station.getServerAddress(), station.getServerPort());
                    ControllersParamsDataBase.map.put(controllerId, new ControllerParamsDto());
                }

                LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "MODE3 controller is built, sent rpcConnectRequest to ");
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {

            if (pingSendFuture == null) {
                System.out.println("PING_CREATED_FUTURE: " + station.getMode3Client().getSocket().getLocalAddress());
                pingSendFuture = executorService.scheduleAtFixedRate(new RpcPingSender(controllerId), 0, 2000, TimeUnit.MILLISECONDS);
                PingFutureDataBase.map.put(controllerId, pingSendFuture); // put in map task
            }
            if (pingSendFuture.isDone() || pingSendFuture.isCancelled()) {
                //               closing and removing the client from the mapControllers
                var mode3Client = Mode3ControllersDataBase.map.get(controllerId);
                mode3Client.closeClientSocket();
                Mode3ControllersDataBase.map.remove(controllerId);

                //Canceling and removing the future_task
                PingFutureDataBase.map.get(controllerId).cancel(true);
                PingFutureDataBase.map.remove(controllerId);
                System.out.println("FUTURE TASK IS DONE: " + pingSendFuture.isDone());
            }
        }

    }
}