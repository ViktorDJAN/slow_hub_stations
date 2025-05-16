package ru.promelectronika.runnables;


import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
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
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    private final SlowChargingStation station;

    private ScheduledFuture<?> pingSendFuture;


    public ControllerStarter(SlowChargingStation slowChargingStation) {
        this.station = slowChargingStation;
    }


    @Override
    public void run() {
        executorService.scheduleAtFixedRate(this::connectToController,0,1000,TimeUnit.MILLISECONDS);





    }

    public void connectToController() {
        Integer controllerId = Integer.parseInt(station.getMode3ClientAddress().substring(10));

        if (!Mode3ControllersDataBase.map.containsKey(controllerId)) {
            try {// CREATING CONTROLLER AND SETTING IT TO STATION
                System.out.println("MAP__" + Mode3ControllersDataBase.map + "   controllerId: " + controllerId);
                var mode3Client = new Mode3Client(station.getConnectorId());
                mode3Client.connectToSocket(station.getMode3ClientAddress(), station.getMode3ClientPort());
                Mode3ControllersDataBase.map.put(controllerId, mode3Client);
                station.setMode3Client(mode3Client);
                mode3Client.rpcLog(true, ConfigsFile.logger_remote_address, ConfigsFile.logger_remote_port);

                // CREATING AND ADDING CONTROLLER PARAMS DTO TO MAP
                if (mode3Client.getSocket().isConnected()) {
                    mode3Client.rpcConnectRequest(station.getServerAddress(), station.getServerPort());
                    ControllersParamsDataBase.map.put(controllerId, new ControllerParamsDto());
                    LoggerPrinter.logAndPrint(ColorKind.BLACK_TEXT, LoggerType.MODE3_LOGGER, "MODE3 CONTROLLER____________________________C_R_E_A_T_E_D___________________________MODE3 hash= " + station.getMode3Client().hashCode() );
                }
                if (PingFutureDataBase.map.containsKey(controllerId)) {
                    PingFutureDataBase.map.get(controllerId).cancel(true);
                }

            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
        if (!PingFutureDataBase.map.containsKey(controllerId)) {
            pingSendFuture = executorService.scheduleAtFixedRate(new RpcPingSender(controllerId), 0, 2000, TimeUnit.MILLISECONDS);
            PingFutureDataBase.map.put(controllerId, pingSendFuture); // put in map task
        }
        try {
            if (PingFutureDataBase.map.get(controllerId).isDone() || PingFutureDataBase.map.get(controllerId).isCancelled()) {
                var mode3Client = Mode3ControllersDataBase.map.get(controllerId);
                mode3Client.closeClientSocket();
                Mode3ControllersDataBase.map.remove(controllerId);
                PingFutureDataBase.map.get(controllerId).cancel(true);
                PingFutureDataBase.map.remove(controllerId);
                LoggerPrinter.logAndPrint(ColorKind.RED_BG_BLACK_TEXT, LoggerType.MODE3_LOGGER,"FUTURE TASK IS DONE: " + pingSendFuture.isDone() + " MODE3_controllers_MAP size = " + Mode3ControllersDataBase.map.size());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}