package ru.promelectronika.runnables;


import functional.concrete_classes.ThreePhaseEnergyMeter;
import functional.data_bases.MeasurementsBase;
import functional.dto.MeasurementDto;
import functional.runnable.EnergyCounterRunner;
import functional.runnable.GetterMeasurements;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.util_stuff.ConfigsFile;
import ru.promelectronika.util_stuff.LoggerPrinter;
import ru.promelectronika.enums.LoggerType;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnergyMeterProcess {
    private static final List<ThreePhaseEnergyMeter> energyMetersList = new ArrayList<>();
    private static final ScheduledExecutorService service = Executors.newScheduledThreadPool(2);


    //  ** Binding energy counters to concrete Measurements dto,to take data from them hereafter.
    public static void fillMeasurementsBase(List<ThreePhaseEnergyMeter> countersList) {
        if (!countersList.isEmpty()) {
            energyMetersList.clear();
            energyMetersList.addAll(countersList);
            countersList.forEach(counter -> MeasurementsBase.map.put(counter.getSlaveId(), new MeasurementDto()));
            LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "ENERGY_METER_PROCESS: MAP_FILLED!  map: " + MeasurementsBase.map);
        }
    }

    //todo check if that method works or not
    public void turnEnergyCountersMetersOn(boolean isOn) {
        if (isOn) {
            int step = 0;
            if (!energyMetersList.isEmpty()) {
                for (ThreePhaseEnergyMeter enMeter : energyMetersList) {
                    if (enMeter.getModbusMaster().isConnected()) {
                        var enMeterRunnable = new EnergyCounterRunner(enMeter.getSlaveId(), new GetterMeasurements(false, ConfigsFile.embedded_en_meter_id, ConfigsFile.outer_en_meter_id), enMeter.getAddress(), enMeter.getModbusMaster());
                        service.scheduleAtFixedRate(enMeterRunnable, step * 600L, energyMetersList.size() * 600L, TimeUnit.MILLISECONDS);
                        step++;
                        LoggerPrinter.logAndPrint(ColorKind.PURPLE_BG_BLACK_TEXT, LoggerType.MAIN_LOGGER, "ENERGY_METER_PROCESS: ENERGY_COUNTER: " + enMeter.getSlaveId() +"   GET_STARTED_TO_WORK!");
                    }
                }
            }
        } else {
            ColorTuner.printCyanText("ENERGY_METER_PROCESS: IS_TURNED: " + isOn);
        }
    }

    public static EnergyMeterProcess createProcess(List<ThreePhaseEnergyMeter> countersList, boolean isOn, boolean isLoggerOn) {
        EnergyMeterProcess energyMeterProcess = new EnergyMeterProcess();
        energyMeterProcess.configure(countersList, isOn, isLoggerOn);
        return new EnergyMeterProcess();
    }

    public void configure(List<ThreePhaseEnergyMeter> energyCounters, boolean isOn, boolean isLoggerOn) {
        fillMeasurementsBase(energyCounters);
        turnEnergyCountersMetersOn(isOn);
    }

    public static void writeMeasurementsIntoLogs(List<ThreePhaseEnergyMeter> countersIdList, boolean isLoggerOn) {
        while (isLoggerOn) {
            for (int i = 0; i < countersIdList.size(); i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    ColorTuner.redBackgroundBlackText(" writeMeasurementsIntoLogs: Interrupted exception: " + e.getMessage());
                }
            }
        }
    }
}
