package ru.promelectronika.runnables;


import ru.promelectronika.util_stuff.ColorTuner;
import ru.promelectronika.dataBases.InnerEnMeterDtoDataBase;
import ru.promelectronika.dataBases.OuterEnMeterDtoDataBase;
import ru.promelectronika.enums.EnergyMeterType;

public class PowerConsumptionRender implements Runnable {
    private int energyMeterId;
    private EnergyMeterType energyMeterType;

    public PowerConsumptionRender(int energyMeterId, EnergyMeterType energyMeterType) {
        this.energyMeterId = energyMeterId;
        this.energyMeterType = energyMeterType;
    }

    @Override
    public void run() {
        try {
            if (energyMeterType.name().equals("INNER_ENERGY_METER")) {
                double consumedPowerInner = InnerEnMeterDtoDataBase.map.get(energyMeterId).getConsumedPower();
                double phaseCurrentInner = InnerEnMeterDtoDataBase.map.get(energyMeterId).getPhaseCurrent();
                double phaseVoltageInner = InnerEnMeterDtoDataBase.map.get(energyMeterId).getPhaseVoltage();
                ColorTuner.yellowBackgroundBlueText("INNER: Consumed_POWER: " + consumedPowerInner + " current: " + phaseCurrentInner + " voltage: " + phaseVoltageInner);

            } else if (energyMeterType.name().equals("OUTER_ENERGY_METER")) {
                double consumedPowerOuter = OuterEnMeterDtoDataBase.map.get(energyMeterId).getAvailablePower();
                double phaseCurrentOuter = OuterEnMeterDtoDataBase.map.get(energyMeterId).getPhaseCurrent();
                double phaseVoltageOuter = OuterEnMeterDtoDataBase.map.get(energyMeterId).getPhaseVoltage();
                ColorTuner.whiteBackgroundBlackText("OUTER: Available_POWER: " + consumedPowerOuter + " phaseCurrent: " + phaseCurrentOuter + " voltage: " + phaseVoltageOuter);
            }
        } catch (Exception e) {
            ColorTuner.blackBackgroundRedText("exception: " + e.getMessage());
//                throw new RuntimeException(e);
        }

    }
}
