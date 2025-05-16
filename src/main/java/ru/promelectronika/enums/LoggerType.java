package ru.promelectronika.enums;

public enum LoggerType {
    MAIN_LOGGER("loggerMain"),
    RPC_SERVER_LOGGER("loggerServer"),
    ENERGY_METER_LOGGER("loggerEnergyMeterProcess"),
    MODE3_LOGGER("loggerMode3Sending"),
    MODE3_RECEIVE("loggerMode3Receive"),
    OCPP_LOGGER("loggerOcpp");

    private final String name;

    LoggerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
