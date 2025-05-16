package ru.promelectronika.runnables;

import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.dataBases.Mode3ControllersDataBase;
import ru.promelectronika.util_stuff.LoggerPrinter;

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
            }
        } catch (RuntimeException e) {
            mode3Client.setConnected(false);
            LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.MODE3_LOGGER, "CAN NOT SEND PING...." + e.getMessage());
            throw new RuntimeException();

        }
    }
}
