package ru.promelectronika.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class InnerEnMeterDto {
    private double consumedPower;
    private double phaseCurrent;
    private double phaseVoltage;
    private Map<String, Double> currentMap;
    private Map<String, Double> voltageMap;


    @Override
    public String toString() {
        return "InnerEnMeterDto{" +
                "consumed_power=" + consumedPower +
                ", phaseCurrent=" + phaseCurrent +
                ", phaseVoltage=" + phaseVoltage +
                ", currentMap=" + currentMap +
                ", voltageMap=" + voltageMap +
                '}';
    }
}
