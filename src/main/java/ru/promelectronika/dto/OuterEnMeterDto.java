package ru.promelectronika.dto;


public class OuterEnMeterDto {
    private double phaseCurrent;
    private double phaseVoltage;
    private double availablePower;

    public double getPhaseCurrent() {
        return phaseCurrent;
    }

    public void setPhaseCurrent(double phaseCurrent) {
        this.phaseCurrent = phaseCurrent;
    }

    public double getPhaseVoltage() {
        return phaseVoltage;
    }

    public void setPhaseVoltage(double phaseVoltage) {
        this.phaseVoltage = phaseVoltage;
    }

    public double getAvailablePower() {
        return availablePower;
    }

    public void setAvailablePower(double availablePower) {
        this.availablePower = availablePower;
    }

    @Override
    public String toString() {
        return "OuterEnMeterDto{" +
                "phaseCurrent=" + phaseCurrent +
                ", phaseVoltage=" + phaseVoltage +
                ", availablePower=" + availablePower +
                '}';
    }
}
