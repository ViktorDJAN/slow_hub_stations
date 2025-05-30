package ru.promelectronika.runnables;


import ru.promelectronika.enums.LoggerType;
import ru.promelectronika.util_stuff.ColorKind;
import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.dataBases.OuterEnMeterDtoDataBase;
import ru.promelectronika.dto.TransferredCurrent;
import ru.promelectronika.enums.EnergyMeterType;
import ru.promelectronika.util_stuff.LoggerPrinter;

import static ru.promelectronika.enums.NetworkType.THREE_PHASE_NETWORK;

public class PhaseCurrentChangesTracker implements Runnable {
    private volatile double previousCurrent = 0;
    private volatile double actualCurrent = 0;
    private final int energyMeterID;
    private final EnergyMeterType energyMeterType;

    public PhaseCurrentChangesTracker(int energyMeterID, EnergyMeterType energyMeterType) {
        this.energyMeterID = energyMeterID;
        this.energyMeterType = energyMeterType;
    }

    @Override
    public void run() {
        if (energyMeterType.name().equals(EnergyMeterType.OUTER_ENERGY_METER.name())) {
            TransferredCurrent.maximumCurrentLimitA = (calculateCurrentChanges());
            ColorTuner.printWhiteText("PhaseCurrentChangesTracker:  TRANSFERRED_CURRENT: " + TransferredCurrent.maximumCurrentLimitA);
        }
    }

    public synchronized double calculateCurrentChanges() {
        double abs = 0;
        try {
            double phaseCurrent1 = OuterEnMeterDtoDataBase.map.get(energyMeterID).getPhaseCurrent();
            if (phaseCurrent1 != 0) {
//                System.out.println("Phase Current: " + phaseCurrent1);
                previousCurrent = actualCurrent;
                actualCurrent = phaseCurrent1;
                abs = Math.abs(previousCurrent - actualCurrent);
            }
            if (abs > 2.0) {
                LoggerPrinter.logAndPrint(ColorKind.GREEN_BG_YELLOW_TEXT, LoggerType.ENERGY_METER_LOGGER, " PhaseCurrentChangesTracker : CHANGES OF CURRENT GRATER THAN 2.0: " + actualCurrent);
                return actualCurrent;
            }
        } catch (Exception e) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        return actualCurrent;
    }
}
