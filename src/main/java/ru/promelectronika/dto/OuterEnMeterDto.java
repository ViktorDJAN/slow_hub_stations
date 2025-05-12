package ru.promelectronika.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OuterEnMeterDto {
    private double phaseCurrent;
    private double phaseVoltage;
    private double availablePower;


    @Override
    public String toString() {
        return "OuterEnMeterDto{" +
                "phaseCurrent=" + phaseCurrent +
                ", phaseVoltage=" + phaseVoltage +
                ", availablePower=" + availablePower +
                '}';
    }
}
