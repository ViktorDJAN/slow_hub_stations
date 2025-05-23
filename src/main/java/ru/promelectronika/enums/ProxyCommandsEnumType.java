package ru.promelectronika.enums;



public enum ProxyCommandsEnumType {
    // For Rpc Controller commands
    DISCONNECTED(0),
    CONNECTED(1),
    PREPARING(2),
    CHARGE(3),
    CHARGE_EV_REQUIRES_VENTILATION(4),
    STOP(5),
    ERROR(6),
    FAULT(7),
    // For initialization
    AVAILABLE_STATE(100),// it is not from protocol RPC

    //OCPP_HANDLER commands
    START_POWER_SUPPLY(20),
    STOP_POWER_SUPPLY(21),
    NETWORK_ERROR(24),

    // STATION_HANDLER commands
    METER_VALUES_TRANSFER(22),
    EMERGENCY_BUTTON_PRESSED(23);

    public int getValue() {
        return value;
    }

    private final int value;

    ProxyCommandsEnumType(int value) {
        this.value = value;
    }


}
