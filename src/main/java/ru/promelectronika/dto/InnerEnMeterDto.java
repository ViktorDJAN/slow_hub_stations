package ru.promelectronika.dto;


import java.util.Map;

public class InnerEnMeterDto {
    private double consumedPower;
    private double phaseCurrent;
    private double phaseVoltage;
    private Map<String, Double> currentMap;
    private Map<String, Double> voltageMap;

    public double getConsumedPower() {
        return consumedPower;
    }

    public void setConsumedPower(double consumedPower) {
        this.consumedPower = consumedPower;
    }

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

    public Map<String, Double> getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map<String, Double> currentMap) {
        this.currentMap = currentMap;
    }

    public Map<String, Double> getVoltageMap() {
        return voltageMap;
    }

    public void setVoltageMap(Map<String, Double> voltageMap) {
        this.voltageMap = voltageMap;
    }

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
