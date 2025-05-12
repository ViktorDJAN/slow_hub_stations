package ru.promelectronika.runnables;

import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.dataBases.Mode3ControllersDataBase;

import java.time.LocalTime;


public class RpcPingSender implements Runnable {
    private final Integer stationId;

    public RpcPingSender(Integer stationId) {
        this.stationId = stationId;
    }

    @Override
    public void run() {
        var mode3Client = Mode3ControllersDataBase.map.get(stationId);
        try {

            if (mode3Client != null) {
                mode3Client.rpcPing();
                mode3Client.setConnected(true);
                ColorTuner.printRedText(stationId+" :PING: " + LocalTime.now());
//                mode3Client.rpcReqSeccCurrentState();
            }
        } catch (RuntimeException e) {
            mode3Client.setConnected(false);
            ColorTuner.redBackgroundBlackText("CAN NOT SEND PING...." + e.getMessage());
            throw new RuntimeException();
            // closing and removing the client from the mapControllers
//            mode3Client.closeClientSocket();
//            Mode3ControllersDataBase.map.remove(stationId);
//
//            //Canceling and removing the future_task
//            FutureTasksDataBase.map.get(stationId).cancel(true);
//            FutureTasksDataBase.map.remove(stationId);
        }
    }
}
