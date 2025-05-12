package ru.promelectronika.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControllerParamsDto {
    //rpcPing
    private volatile int selfConnectionInputState;
    private volatile int selfConnectionOutputState;

    //SET_FW_VERSION
    private volatile String version;

    //SET_PROTOCOL_VERSION
    private volatile String protocolVersion;

    //SET_SECC_CURRENT_STATE
    private volatile int chargeState = 0;
    private volatile String chargeStateProtocolSpecific;



    // For Logging

    public String getRpcPing() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tConnection Input State: ");
        str.append(selfConnectionInputState);
        str.append("\n\tConnection Output State: ");
        str.append(selfConnectionOutputState);
        return str.toString();
    }

    public String getSetFwVersion() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tVersion: ");
        str.append(version);
        return str.toString();
    }

    public String getSetProtocolVersion() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tVersion: ");
        str.append(protocolVersion);
        return str.toString();
    }

    public String getSetSeccCurrentState() {
        StringBuilder str = new StringBuilder();
        str.append("\n\tCharge state: ");
        str.append(chargeState);
        str.append("\n\tCharge state protocol specific: ");
        str.append(chargeStateProtocolSpecific);
        return str.toString();
    }

    @Override
    public String toString() {
        return "ControllerParamsDto{" +
                "selfConnectionInputState=" + selfConnectionInputState +
                ", selfConnectionOutputState=" + selfConnectionOutputState +
                ", version='" + version + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", chargeState=" + chargeState +
                ", chargeStateProtocolSpecific='" + chargeStateProtocolSpecific + '\'' +
                '}';
    }
}
